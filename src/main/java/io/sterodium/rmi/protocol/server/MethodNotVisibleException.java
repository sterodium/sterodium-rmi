package io.sterodium.rmi.protocol.server;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * Method is found, but has no 'public' visibility.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 17.11.2015
 */
public class MethodNotVisibleException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Method %s.%s not visible (parameter types: %s)";

    public MethodNotVisibleException(Class<?> methodClass, String methodName, Class<?>[] parameterTypes) {
        super(format(MESSAGE_TEMPLATE, methodClass.getName(), methodName, Arrays.toString(parameterTypes)));
    }

}
