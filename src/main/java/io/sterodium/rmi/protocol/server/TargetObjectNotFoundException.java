package io.sterodium.rmi.protocol.server;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 17.11.2015
 */
public class TargetObjectNotFoundException extends RmiException {

    public TargetObjectNotFoundException(String message) {
        super(message);
    }

    public TargetObjectNotFoundException(int errorCode, String errorMessage, String errorDetails) {
        super(errorCode, errorMessage, errorDetails);
    }
}
