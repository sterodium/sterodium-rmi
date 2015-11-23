package io.sterodium.rmi.protocol.client;

import java.io.IOException;

/**
 * Class is responsible for switchable transport component
 * (protocol is transport agnostic).
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20.11.2015
 */
public interface RmiTransport {

    String send(String objectId, String json) throws IOException;

}
