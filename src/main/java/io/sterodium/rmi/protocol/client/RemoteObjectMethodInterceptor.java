package io.sterodium.rmi.protocol.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Primitives;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import io.sterodium.rmi.protocol.server.MethodInvocationException;
import io.sterodium.rmi.protocol.server.MethodNotFoundException;
import io.sterodium.rmi.protocol.server.MethodParameterException;
import io.sterodium.rmi.protocol.server.RmiException;
import io.sterodium.rmi.protocol.server.RmiProtocol;
import io.sterodium.rmi.protocol.server.TargetObjectNotFoundException;
import io.sterodium.rmi.protocol.server.UnsupportedProtocolException;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static io.sterodium.rmi.protocol.json.PrimitiveTypes.*;
import static java.lang.String.format;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class RemoteObjectMethodInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteObjectMethodInterceptor.class);

    private RemoteObjectProxyFactory proxyFactory;

    private RemoteInvoker invoker;

    private String widgetId;

    public RemoteObjectMethodInterceptor(RemoteObjectProxyFactory proxyFactory, RemoteInvoker invoker, String widgetId) {
        this.proxyFactory = proxyFactory;
        this.invoker = invoker;
        this.widgetId = widgetId;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        // TODO: MVO: exclude equals() / hashCode()
        if (Object.class.equals(method.getDeclaringClass())) {
            return methodProxy.invokeSuper(proxy, arguments);
        } else if ("toString".equals(method.getName()) && arguments.length == 0) {
            return methodProxy.invokeSuper(proxy, arguments);
        }
        return invoke(method, arguments);
    }

    /**
     * Invokes method remotely. Processes remote method invocation result
     * and constructs object for corresponding type.
     * <p>
     * {@link MethodInvocationResultDto#getValue} is null return null;
     * {@link Method#getReturnType} is {@value Void} return null;
     * {@link Method#getReturnType} is primitive or wrapper - return primitive object of response value;
     * {@link Method#getReturnType} is non primitive - return proxy for class with response value as widget id
     *
     * @param method    intercepted method
     * @param arguments intercepted method arguments
     * @return object of return type or null
     */
    Object invoke(Method method, Object[] arguments) {
        try {
            MethodInvocationResultDto methodResponse = invoker.invoke(widgetId, method, arguments);
            checkErrorCodes(methodResponse);
            String responseValue = methodResponse.getValue();
            return responseValue == null ? null : convertToType(responseValue, method.getReturnType(), methodResponse.getType());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unmapped response", e);
        }
        return null;
    }

    @VisibleForTesting
    protected void checkErrorCodes(MethodInvocationResultDto response) {
        MethodInvocationResultDto.Error error = response.getError();
        if (error == null) {
            return;
        }
        final int code = error.getCode();
        String message = error.getMessage();
        String details = error.getData();
        switch (code) {
            case RmiProtocol.ERROR_CODE_INVALID_JSON:
                throw new UnsupportedProtocolException(code, message, details);
            case RmiProtocol.ERROR_CODE_INVALID_REQUEST:
                throw new UnsupportedProtocolException(code, message, details);
            case RmiProtocol.ERROR_CODE_METHOD_NOT_FOUND:
                throw new MethodNotFoundException(code, message, details);
            case RmiProtocol.ERROR_CODE_INVALID_PARAMS:
                throw new MethodParameterException(code, message, details);
            case RmiProtocol.ERROR_CODE_JSONRPC_ERROR:
                throw new UnsupportedProtocolException(code, message, details);
            case RmiProtocol.ERROR_CODE_SERVER_ERROR:
                throw new MethodInvocationException(code, message, details);
            case RmiProtocol.ERROR_CODE_OBJECT_NOT_FOUND:
                throw new TargetObjectNotFoundException(code, message, details);
            default:
                LOGGER.warn("Unsupported error code '{}': '{}' ({}))", code, message, details);
                throw new RmiException(code, message, details);
        }
    }

    private Object convertToType(String response, Class<?> type, String responseType) throws ClassNotFoundException {
        LOGGER.info(format("Converting response '%s' to type %s or %s", response, type, responseType));
        if (isVoid(type)) {
            return null;
        } else if (String.class.equals(type)) {
            return response;
        } else if (isCharacter(type)) {
            return response.toCharArray()[0];
        } else if (isPrimitive(type)) {
            return parseString(response, type);
        }
        return proxyFactory.create(Class.forName(responseType), response);
    }

    Object parseString(String value, Class<?> type) {
        try {
            Class<?> targetType = Primitives.wrap(type);
            return targetType.getConstructor(String.class).newInstance(value);
        } catch (Exception e) { //throws exception on type = Character.class
            LOGGER.error("Failed to parse string and construct object", e);
            return null;
        }
    }
}
