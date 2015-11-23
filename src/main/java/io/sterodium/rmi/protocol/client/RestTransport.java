package io.sterodium.rmi.protocol.client;

import io.sterodium.rmi.protocol.server.TargetObjectNotFoundException;
import io.sterodium.rmi.protocol.server.TargetServerException;
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

import java.io.IOException;
import java.net.URLEncoder;

import static java.lang.String.format;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class RestTransport implements RmiTransport {

    private final HttpHost httpHost;
    private HttpClient httpClient = HttpClientBuilder.create().build();
    private String path;

    public RestTransport(String host, int port, String path) {
        this.httpHost = new HttpHost(host, port);
        this.path = path;
    }

    @Override
    public String send(String objectId, String json) throws IOException {
        HttpPost request = new HttpPost(path + "/" + URLEncoder.encode(objectId, "UTF-8"));
        request.setEntity(new StringEntity(json));

        HttpResponse httpResponse = httpClient.execute(httpHost, request);

        int statusCode = getStatusCode(httpResponse);
        if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new TargetObjectNotFoundException(format("Widget %s is not available at %s", objectId, getUrl(request)));
        } else if (statusCode != HttpStatus.SC_OK) {
            String messageFromServer = EntityUtils.toString(httpResponse.getEntity());
            String message = "Widget %s invocation failed (expected 200, but was %s): %s";
            throw new TargetServerException(format(message, objectId, statusCode, messageFromServer));
        }
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public void reset() throws IOException {
        HttpDelete request = new HttpDelete(path);
        HttpResponse httpResponse = httpClient.execute(httpHost, request);
        int statusCode = getStatusCode(httpResponse);
        if (statusCode != 200) {
            throw new RuntimeException(format("Widgets reset operation failed (expected 200, but was %s)", statusCode));
        }
    }

    private int getStatusCode(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }

    private String getUrl(HttpRequestBase request) {
        return format("(%s) http://%s%s", request.getMethod(), httpHost.toHostString(), request.getURI());
    }

}
