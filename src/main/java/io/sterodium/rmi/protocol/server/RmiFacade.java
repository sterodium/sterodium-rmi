package io.sterodium.rmi.protocol.server;

import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 24/09/2015
 */
public class RmiFacade {

    private static final Logger LOG = LoggerFactory.getLogger(RmiFacade.class);

    private ObjectLocator objectLocator = new ObjectLocator();

    private Marshaller marshaller = new Marshaller(objectLocator);

    private MethodInvoker invoker = new MethodInvoker(objectLocator);

    public MethodInvocationResultDto invoke(String objectId, MethodInvocationDto invocation) {

        // object location
        Object object = objectLocator.get(objectId);
        if (object == null) {
            LOG.error("Object ({}) not found. Please make sure objects cache size (for objects chained invocation).", objectId);
        }

        // method invocation
        Object result;
        Class<?> returnType = null;
        try {
            MethodInvoker.InvocationResult invocationResult = invoker.invoke(object, invocation);
            result = invocationResult.getResult();
            returnType = invocationResult.getResultClass();
        } catch (RuntimeException e) {
            throw new RemoteMethodInvocationException(e.getMessage(), e);
        }

        // serializing invocation result
        return marshaller.toResponse(result, returnType);
    }

    public void add(String objectId, Object object) {
        objectLocator.addPermanentObject(objectId, object);
    }

}
