package io.sterodium.rmi.protocol.client;

import io.sterodium.rmi.protocol.server.RmiFacade;
import io.sterodium.rmi.protocol.server.RmiProtocol;

import java.io.IOException;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20.11.2015
 */
public class JvmTransport implements RmiTransport {

    private RmiFacade rmiFacade = new RmiFacade();

    public JvmTransport(String objectId, Object object) {
        rmiFacade.add(objectId, object);
    }

    @Override
    public String send(String objectId, String json) throws IOException {
        return new RmiProtocol(objectId, rmiFacade).invoke(json);
    }

}
