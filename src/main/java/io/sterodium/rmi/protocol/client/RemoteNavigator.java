package io.sterodium.rmi.protocol.client;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
public class RemoteNavigator {

    private final RemoteObjectProxyFactory proxyFactory;

    public RemoteNavigator(String host, int port, String path) {
        RestClient restClient = new RestClient(host, port, path);
        this.proxyFactory = new RemoteObjectProxyFactory(new RemoteInvoker(restClient));
    }

    public RemoteNavigator(String objectId, Object object) {
        RestClient restClient = new RestClient(new JvmTransport(objectId, object));
        this.proxyFactory = new RemoteObjectProxyFactory(new RemoteInvoker(restClient));
    }

    public <T> T createProxy(Class<T> clazz, String objectId) {
        return proxyFactory.create(clazz, objectId);
    }
}
