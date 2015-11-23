package io.sterodium.rmi.protocol.server;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 17.11.2015
 */
public class UnsupportedProtocolException extends RmiException {

    public UnsupportedProtocolException(int errorCode, String errorMessage, String errorDetails) {
        super(errorCode, errorMessage, errorDetails);
    }

}
