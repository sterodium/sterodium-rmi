package io.sterodium.rmi.protocol.server;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 19/10/2015
 */
public class MethodInvocationException extends RmiException {

    private static final String MESSAGE_TEMPLATE = "Method %s.%s (parameter types: %s) threw exception %s";

    public MethodInvocationException(Class<?> methodClass, String methodName, Object[] arguments, Throwable e) {
        super(format(MESSAGE_TEMPLATE, methodClass.getName(), methodName, Arrays.toString(arguments), e));
    }

    public MethodInvocationException(int errorCode, String errorMessage, String errorDetails) {
        super(errorCode, errorMessage, errorDetails);
    }

}
