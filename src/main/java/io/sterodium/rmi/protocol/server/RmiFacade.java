package io.sterodium.rmi.protocol.server;

import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.sterodium.rmi.protocol.server.RmiProtocol.*;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 24/09/2015
 */
public class RmiFacade {

    private static final Logger LOG = LoggerFactory.getLogger(RmiFacade.class);

    private static final String CACHE_SIZE_WARNING = "Please make sure objects cache size is big enough for temporary objects (in chained invocation).";

    private ObjectLocator objectLocator = new ObjectLocator();

    private Marshaller marshaller = new Marshaller(objectLocator);

    private MethodInvoker invoker = new MethodInvoker(objectLocator);

    public MethodInvocationResultDto invoke(String objectId, MethodInvocationDto invocation) {

        // object location
        Object object = objectLocator.get(objectId);
        if (object == null) {
            String message = String.format("Object (%s) is not found.", objectId);
            LOG.error(message);
            LOG.error(CACHE_SIZE_WARNING);
            return new MethodInvocationResultDto(ERROR_CODE_OBJECT_NOT_FOUND, message, CACHE_SIZE_WARNING);
        }

        // method invocation
        Object result;
        Class<?> returnType;
        try {
            MethodInvoker.InvocationResult invocationResult = invoker.invoke(object, invocation);
            result = invocationResult.getResult();
            returnType = invocationResult.getResultClass();
        } catch (MethodNotFoundException e) {
            return new MethodInvocationResultDto(ERROR_CODE_METHOD_NOT_FOUND, "Method not found", e.getMessage());
        } catch (MethodNotVisibleException e) {
            return new MethodInvocationResultDto(ERROR_CODE_METHOD_NOT_FOUND, "Method not visible", e.getMessage());
        } catch (MethodParameterException e) {
            return new MethodInvocationResultDto(ERROR_CODE_INVALID_PARAMS, "Invalid params", e.getMessage());
        } catch (MethodInvocationException e) {
            return new MethodInvocationResultDto(ERROR_CODE_SERVER_ERROR, "Method invocation error", e.getMessage());
        } catch (RuntimeException e) {
            LOG.error("Something went wrong during remote method invocation", e);
            return new MethodInvocationResultDto(-32603, "Protocol error", e.getMessage());
        }

        // serializing invocation result
        return marshaller.toResponse(result, returnType);
    }

    public void add(String objectId, Object object) {
        objectLocator.addPermanentObject(objectId, object);
    }

}
