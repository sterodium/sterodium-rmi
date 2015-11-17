package io.sterodium.rmi.protocol.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;

/**
 * Represents stateless RMI protocol - modification of JSON-RPC 2.0:
 * - request object modification: additional field "parameter types" to resolve overloaded methods in Java
 * - request object modification: "arguments" field instead of "params"
 * - response object modification: additional field "result type" to create implementation specific proxies
 * - response object modification: "value" field instead of "result"
 * - protocol doesn't support batch requests
 * - protocol doesn't support notification requests
 * <p>
 * Protocol is transport agnostic (same process, sockets, http, etc.).
 * Protocol uses JSON as data format.
 * <p>
 * Protocol is NOT responsible for (remote) object (to invoke method on) location.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 16.11.2015
 */
public class RmiProtocol {

    public static final int ERROR_CODE_INVALID_JSON = -32700;

    public static final int ERROR_CODE_INVALID_REQUEST = -32600;

    public static final int ERROR_CODE_METHOD_NOT_FOUND = -32601;

    public static final int ERROR_CODE_INVALID_PARAMS = -32602;

    public static final int ERROR_CODE_JSONRPC_ERROR = -32603;

    public static final int ERROR_CODE_SERVER_ERROR = -32000;

    public static final int ERROR_CODE_OBJECT_NOT_FOUND = -32004;

    protected static final String VERSION = "0.1.rmi";

    private static final Gson GSON = new Gson();

    private final String objectId;

    private final RmiFacade rmi;

    public RmiProtocol(String objectId, RmiFacade rmi) {
        this.objectId = objectId;
        this.rmi = rmi;
    }

    public String invoke(String invocationJson) {

        MethodInvocationDto invocation;
        try {
            invocation = GSON.fromJson(invocationJson, MethodInvocationDto.class);
        } catch (JsonSyntaxException e) {
            String details = e.getMessage() + " in JSON: '" + invocationJson + "'";
            return marshall(GSON, new MethodInvocationResultDto(ERROR_CODE_INVALID_JSON, "Invalid JSON", details), null);
        }

        if (!isSupportedProtocol(invocation)) {
            String message = "Unsupported protocol version";
            String version = invocation.getJsonrpc();
            return marshall(GSON, new MethodInvocationResultDto(ERROR_CODE_JSONRPC_ERROR, message, version), invocation);
        }

        if (!isValidRequest(invocation)) {
            return marshall(GSON, new MethodInvocationResultDto(ERROR_CODE_INVALID_REQUEST, "Invalid Request", invocationJson), invocation);
        }

        MethodInvocationResultDto result = rmi.invoke(objectId, invocation);
        return marshall(GSON, result, invocation);
    }

    private boolean isSupportedProtocol(MethodInvocationDto invocation) {
        String version = invocation.getJsonrpc();
        return version == null || version.equals(VERSION);
    }

    private boolean isValidRequest(MethodInvocationDto invocation) {
        String method = invocation.getMethod();
        return method != null && !method.isEmpty();
    }

    private String marshall(Gson gson, MethodInvocationResultDto result, MethodInvocationDto invocation) {
        result.setJsonrpc(VERSION);
        result.setId("" + null);
        if (invocation != null) {
            result.setId(invocation.getId());
        }
        return gson.toJson(result);
    }

}
