package io.sterodium.rmi.protocol.client;

import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class RemoteInvoker {

    public static final Logger LOGGER = LoggerFactory.getLogger(RemoteInvoker.class);

    private static final Gson GSON = new Gson();

    private RestClient restClient;

    public RemoteInvoker(RestClient restClient) {
        this.restClient = restClient;
    }

    public MethodInvocationResultDto invoke(String widgetId, Method method, Object[] arguments) {
        MethodInvocationDto methodInvocationDto = getMethodInvocation(method.getName(), method.getParameterTypes(), arguments);

        String response;
        try {
            response = restClient.invoke(widgetId, methodInvocationDto);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        LOGGER.debug("Response for remote invocation: " + response);

        return GSON.fromJson(response, MethodInvocationResultDto.class);
    }

    @VisibleForTesting
    protected MethodInvocationDto getMethodInvocation(String methodName, Class<?>[] argumentTypes, Object[] arguments) {
        Preconditions.checkArgument(argumentTypes.length == arguments.length);

        // collecting arguments classes names
        String[] argumentClasses = new String[argumentTypes.length];
        String[] stringArguments = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentClasses[i] = argumentTypes[i].getName();
            stringArguments[i] = toString(arguments[i]);
        }

        return new MethodInvocationDto(methodName, argumentClasses, stringArguments);
    }

    private String toString(Object argument) {
        if (RemoteObjectProxyFactory.isProxy(argument)) {
            return RemoteObjectProxyFactory.getObjectId(argument);
        }
        if (argument instanceof CharSequence) {
            return argument.toString();
        }
        return GSON.toJson(argument);
    }
}
