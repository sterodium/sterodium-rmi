package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
public class MethodInvocationDto {

    private final String method;

    private final String[] arguments;

    private final String[] argumentClasses;

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
        return argumentClasses;
    }

    public String[] getArguments() {
        return arguments;
    }

}
