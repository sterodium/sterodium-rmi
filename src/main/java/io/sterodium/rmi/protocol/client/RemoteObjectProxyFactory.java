package io.sterodium.rmi.protocol.client;

import com.google.common.base.Preconditions;
import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import org.objenesis.ObjenesisStd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class RemoteObjectProxyFactory {

    private static Map<Object, String> widgetIds = new HashMap<>();

    private RemoteInvoker invoker;

    public RemoteObjectProxyFactory(RemoteInvoker invoker) {
        this.invoker = invoker;
    }

    public void setInvoker(RemoteInvoker invoker) {
        this.invoker = invoker;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz, String widgetId) {

        // creating proxy class
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setUseFactory(true);
        enhancer.setCallbackType(RemoteObjectMethodInterceptor.class);
        if (clazz.getSigners() != null) {
            enhancer.setNamingPolicy(NAMING_POLICY_FOR_CLASSES_IN_SIGNED_PACKAGES);
        }
        Class<?> proxyClass = enhancer.createClass();

        // instantiating class without constructor call
        ObjenesisStd objenesis = new ObjenesisStd();
        Factory proxy = (Factory) objenesis.newInstance(proxyClass);
        proxy.setCallbacks(new Callback[]{new RemoteObjectMethodInterceptor(this, invoker, widgetId)});
        T widget = (T) proxy;

        widgetIds.put(widget, widgetId);
        return widget;
    }

    public <T> T castWidgetTo(Object widget, Class<T> clazz) {
        String errorMessage = "Given widget has not been created by instance of current " + RemoteObjectProxyFactory.class.getSimpleName();
        Preconditions.checkArgument(Enhancer.isEnhanced(widget.getClass()), errorMessage);
        String widgetId = widgetIds.get(widget);
        return create(clazz, widgetId);
    }

    public void reset() {
        widgetIds.clear();
    }

    public static boolean isProxy(Object proxyCandidate) {
        return widgetIds.get(proxyCandidate) != null;
    }

    public static String getObjectId(Object proxy) {
        return widgetIds.get(proxy);
    }

    private static final NamingPolicy NAMING_POLICY_FOR_CLASSES_IN_SIGNED_PACKAGES = new DefaultNamingPolicy() {
        @Override
        public String getClassName(String prefix, String source, Object key, Predicate names) {
            return "codegen." + super.getClassName(prefix, source, key, names);
        }
    };

}
