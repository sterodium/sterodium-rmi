package io.sterodium.rmi.protocol.server;

/**
 * Base class for all protocol specific exceptions.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20.11.2015
 */
public class RmiException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Error '%s': '%s' (%s)";

    public RmiException(String message) {
        super(message);
    }

    public RmiException(int errorCode, String errorMessage, String errorDetails) {
        super(String.format(MESSAGE_TEMPLATE, errorCode, errorMessage, errorDetails));
    }

}
