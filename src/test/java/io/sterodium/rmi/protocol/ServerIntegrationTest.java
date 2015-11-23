package io.sterodium.rmi.protocol;

import io.sterodium.rmi.protocol.server.RmiFacade;
import io.sterodium.rmi.protocol.server.RmiProtocol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 16.11.2015
 */
public class ServerIntegrationTest {

    private RmiFacade rmi;

    private RmiProtocol protocol;

    @Before
    public void setUp() {
        rmi = new RmiFacade();
        rmi.add("myObj", new MyObject());
        protocol = new RmiProtocol("myObj", rmi);
    }

    @Test
    public void shouldReturnObjectNotFoundError() {
        RmiProtocol protocol = new RmiProtocol("unknownObjectId", rmi);
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: doSomething, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32004," +
                "message: Object (unknownObjectId) is not found.," +
                "data: Please make sure objects cache size is big enough for temporary objects (in chained invocation).}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnInvalidJsonError() {
        String result = protocol.invoke("{corruptedRequest...");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32700," +
                "message: Invalid JSON," +
                "data: java.io.EOFException:Endofinputatline1column21 in JSON: '{corruptedRequest...'" +
                "}," +
                "id: null" +
                "}", result);
    }

    @Test
    public void shouldReturnInvalidRequestError() {
        String result = protocol.invoke("{id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32600," +
                "message: Invalid Request," +
                "data: \"{id: 1}\"}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnInvalidMethodError() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: unknownMethod, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32601," +
                "message: Method not found," +
                "data: Method io.sterodium.rmi.protocol.MyObject.unknownMethod not found (parameter types:[])}," +
                "id: 1" +
                "}", result);
    }

    @Test
     public void shouldReturnInvisibleMethodError() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: protectedMethod, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32601," +
                "message: Method not visible," +
                "data: Method io.sterodium.rmi.protocol.MyObject.protected Method not visible (parameter types:[])}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnInvalidParamsError_ClassNotFound() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: acceptIntParameter, arguments: [1L], argumentClasses: [com.company.NonExistingClass], id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32602," +
                "message: Invalid params," +
                "data: Method parameter #1: class com.company.NonExistingClass not found}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnInvalidParamsError_WrongValueType() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: acceptIntParameter, arguments: [1L], argumentClasses: [long], id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32602," +
                "message: Invalid params," +
                "data: Method parameter #1: value 1L could not be mapped to type long}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnProtocolError() {
        String result = protocol.invoke("{jsonrpc: unsupported-version, method: publicMethod, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32603," +
                "message: Unsupported protocol version," +
                "data: unsupported-version}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnMethodInvocationError() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: exceptionThrowingMethod, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, error: {" +
                "code: -32000," +
                "message: Method invocation error," +
                "data: Method io.sterodium.rmi.protocol.MyObject.exceptionThrowingMethod (parameter types:[])" +
                "threw exception java.lang.RuntimeException:RMI protocol should handle me}," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnSuccessfulResult() {
        String result = protocol.invoke("{jsonrpc: 0.1.rmi, method: publicMethod, id: 1}");
        assertJson("{jsonrpc: 0.1.rmi, " +
                "value: OK," +
                "type: java.lang.String," +
                "id: 1" +
                "}", result);
    }

    @Test
    public void shouldReturnSuccessfulResult_oldProtocolClient() {
        String result = protocol.invoke("{method: publicMethod}");
        assertJson("{jsonrpc: 0.1.rmi, " +
                "value: OK," +
                "type: java.lang.String" +
                "}", result);
    }

    private void assertJson(String expected, String actual) {
        assertEquals(trim(expected), trim(actual));
    }

    private String trim(String json) {
        return json.replaceAll("\\\\\"", "").replaceAll("\"", "").replaceAll("\\s", "").replaceAll("\\\\u0027", "'");
    }

}
