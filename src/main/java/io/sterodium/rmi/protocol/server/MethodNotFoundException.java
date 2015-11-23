package io.sterodium.rmi.protocol.server;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * Method (public or non-public) is not found for given class
 * (and all its superclasses) and for given method parameter types.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 17.11.2015
 */
public class MethodNotFoundException extends RmiException {

    private static final String MESSAGE_TEMPLATE = "Method %s.%s not found (parameter types: %s)";

    public MethodNotFoundException(Class<?> methodClass, String methodName, Class<?>[] parameterTypes) {
        super(format(MESSAGE_TEMPLATE, methodClass.getName(), methodName, Arrays.toString(parameterTypes)));
    }

    public MethodNotFoundException(int errorCode, String errorMessage, String errorDetails) {
        super(errorCode, errorMessage, errorDetails);
    }
}
