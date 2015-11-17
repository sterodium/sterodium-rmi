package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 16.11.2015
 */
public class MyObject {

    public String publicMethod() {
        return "OK";
    }

    public String exceptionThrowingMethod() {
        throw new RuntimeException("RMI protocol should handle me");
    }

    protected String protectedMethod() {
        return "I am invisible for RMI protocol";
    }

    public String acceptLongParameter(long parameter) {
        return "OK";
    }

}
