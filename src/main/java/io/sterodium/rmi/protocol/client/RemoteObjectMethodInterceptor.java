package io.sterodium.rmi.protocol.client;

import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import com.google.common.primitives.Primitives;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

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
     * <p/>
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
            LOGGER.info("Response: " + methodResponse.toString());

            String responseValue = methodResponse.getValue();
            return responseValue == null ? null : convertToType(responseValue, method.getReturnType(), methodResponse.getType());
        } catch (Exception e) {
            LOGGER.error("Unmapped response", e);
        }
        return null;
    }

    private Object convertToType(String response, Class<?> type, String responseType) throws ClassNotFoundException {
        LOGGER.info(String.format("Converting response '%s' to type %s or %s", response, type, responseType));
        if (isVoid(type)) {
            return null;
        }
        if (String.class.equals(type)) {
            return response;
        }
        if (isNotVoidPrimitive(type)) {
            return parseString(response, type);
        }
        return proxyFactory.create(Class.forName(responseType), response);
    }

    boolean isVoid(Class<?> returnType) {
        return void.class.equals(returnType) || Void.class.equals(returnType);
    }

    boolean isNotVoidPrimitive(Class<?> type) {
        return !isVoid(type) && (type.isPrimitive() || Primitives.isWrapperType(type));
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
