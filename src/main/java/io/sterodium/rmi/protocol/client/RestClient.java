package io.sterodium.rmi.protocol.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private static final Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Class.class, new ClassAdapter());
        GSON = gsonBuilder.create();
    }

    private final RmiTransport transport;

    public RestClient(String host, int port, String path) {
        transport = new RestTransport(host, port, path);
    }

    public RestClient(RmiTransport transport) {
        this.transport = transport;
    }

    public String invoke(String remoteObjectId, MethodInvocationDto methodInvocationDto) throws IOException {
        String json = GSON.toJson(methodInvocationDto);
        LOGGER.info("Request: " + json);
        String response = transport.send(remoteObjectId, json);
        LOGGER.info("Response: " + response);
        return response;
    }

}
