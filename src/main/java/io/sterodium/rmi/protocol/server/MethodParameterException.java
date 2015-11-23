package io.sterodium.rmi.protocol.server;


import static java.lang.String.format;

/**
 * Either method parameter class couldn't be loaded
 * or method parameter value couldn't be mapped to
 * according method parameter type provided.
 * <p>
 * Parameter index is 1-based.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 17.11.2015
 */
public class MethodParameterException extends RmiException {

    private static final String CLASS_LOADING_MESSAGE_TEMPLATE = "Method parameter #%s: class %s not found";

    private static final String WRONG_TYPE_MESSAGE_TEMPLATE = "Method parameter #%s: value \"%s\" could not be mapped to type %s";

    public MethodParameterException(int parameterIndex, Class<?> parameterClass, String parameterValue) {
        super(format(WRONG_TYPE_MESSAGE_TEMPLATE, parameterIndex, parameterValue, parameterClass));
    }

    public MethodParameterException(int parameterIndex, String argumentClassName) {
        super(format(CLASS_LOADING_MESSAGE_TEMPLATE, parameterIndex, argumentClassName));
    }

    public MethodParameterException(int errorCode, String errorMessage, String errorDetails) {
        super(errorCode, errorMessage, errorDetails);
    }
}
