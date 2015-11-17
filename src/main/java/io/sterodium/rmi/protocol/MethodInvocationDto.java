package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
public class MethodInvocationDto {

    // protocol version
    private String jsonrpc;
    private final String method;
    private final String[] arguments;
    private final String[] argumentClasses;
    private String id;

    /**
     * Represents information required to invoke method on the object remotely
     *
     * @param method          method name {@link java.lang.reflect.Method#getName()}
     * @param argumentClasses ordered array of method argument classes
     * @param arguments       ordered values for method arguments
     */
    public MethodInvocationDto(String method, String[] argumentClasses, String[] arguments) {
        this.method = method;
        this.argumentClasses = argumentClasses;
        this.arguments = arguments;
    }

    public String getMethod() {
        return method;
    }

    public String[] getArgumentClasses() {
        return argumentClasses == null ? new String[]{} : argumentClasses;
    }

    public String[] getArguments() {
        return arguments == null ? new String[]{} : arguments;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
