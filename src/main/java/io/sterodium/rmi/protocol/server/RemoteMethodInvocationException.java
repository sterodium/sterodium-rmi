package io.sterodium.rmi.protocol.server;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 19/10/2015
 */
public class RemoteMethodInvocationException extends RuntimeException {
    public RemoteMethodInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
