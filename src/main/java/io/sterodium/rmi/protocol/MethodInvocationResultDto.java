package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
public class MethodInvocationResultDto {

    private String value;
    private String type;

    /**
     * Represents result of method invocation which is sent back to the client.
     *
     * @param value string representation of resulting object.
     *              Null for {@link java.lang.Void}
     * @param type  a name of a resulting class {@link Class#getName}
     */
    public MethodInvocationResultDto(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MethodInvocationResultDto{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
