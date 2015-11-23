package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
public class MethodInvocationResultDto {

    private String jsonrpc;
    private String value;
    private String type;
    private Error error;
    private String id;

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

    public MethodInvocationResultDto(int errorCode, String shortErrorMessage, String detailedErrorMessage) {
        this.error = new Error(errorCode, shortErrorMessage, detailedErrorMessage);
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Error getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MethodInvocationResultDto{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * Class contains such error details as:
     *  - error code
     *  - short message
     *  - detailed error information, nested errors, etc.
     *
     * See http://www.jsonrpc.org/specification#error_object
     */
    public static class Error {

        private int code;

        private String message;

        private String data;

        public Error(int code, String message, String data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getData() {
            return data;
        }
    }
}
