package io.sterodium.rmi.protocol.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

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

    private final HttpHost httpHost;
    private HttpClient httpClient = HttpClientBuilder.create().build();
    private String path;

    public RestClient(String host, int port, String path) {
        this.httpHost = new HttpHost(host, port);
        this.path = path;
    }

    public String invoke(String remoteObjectId, MethodInvocationDto methodInvocationDto) throws IOException {
        String jsonString = GSON.toJson(methodInvocationDto);

        LOGGER.info("Invocation request: " + jsonString);

        HttpPost request = new HttpPost(path + "/" + URLEncoder.encode(remoteObjectId, "UTF-8"));
        request.setEntity(new StringEntity(jsonString));

        HttpResponse httpResponse = httpClient.execute(httpHost, request);

        int statusCode = getStatusCode(httpResponse);
        if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new RuntimeException(String.format("Widget %s is not available at %s", remoteObjectId, getUrl(request)));
        } else if (statusCode != HttpStatus.SC_OK) {
            String messageFromServer = EntityUtils.toString(httpResponse.getEntity());
            throw new RuntimeException(String.format("Widget %s invocation failed (expected 200, but was %s): %s",
                    remoteObjectId, statusCode, messageFromServer));
        }
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public void reset() throws IOException {
        HttpDelete request = new HttpDelete(path);
        HttpResponse httpResponse = httpClient.execute(httpHost, request);
        int statusCode = getStatusCode(httpResponse);
        if (statusCode != 200) {
            throw new RuntimeException(String.format("Widgets reset operation failed (expected 200, but was %s)", statusCode));
        }
    }

    private int getStatusCode(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }

    private String getUrl(HttpRequestBase request) {
        return String.format("(%s) http://%s%s", request.getMethod(), httpHost.toHostString(), request.getURI());
    }

    private static class ClassAdapter implements JsonSerializer<Class> {
        @Override
        public JsonElement serialize(Class clazz, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(clazz.getName());
        }
    }
}
