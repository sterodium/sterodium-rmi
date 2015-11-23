package io.sterodium.rmi.protocol;

import io.sterodium.rmi.protocol.client.RemoteNavigator;
import io.sterodium.rmi.protocol.server.MethodInvocationException;
import io.sterodium.rmi.protocol.server.MethodNotFoundException;
import io.sterodium.rmi.protocol.server.TargetObjectNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 20.11.2015
 */
public class ClientServerIntegrationTest {

    private MyObject remoteObject;

    @Before
    public void setUp() {
        RemoteNavigator navigator = new RemoteNavigator("myObj", new MyObject());
        remoteObject = navigator.createProxy(MyObject.class, "myObj");
    }

    @Test
    public void publicMethod() {
        assertEquals("OK", remoteObject.publicMethod());
    }

    @Test(expected = TargetObjectNotFoundException.class)
    public void objectNotFound() {
        RemoteNavigator navigator = new RemoteNavigator("myObj", new MyObject());
        remoteObject = navigator.createProxy(MyObject.class, "nonExistingObject");
        remoteObject.publicMethod();
    }

    @Test(expected = MethodNotFoundException.class)
    public void invalidMethod() {
        RemoteNavigator navigator = new RemoteNavigator("myObj", new Object());
        remoteObject = navigator.createProxy(MyObject.class, "myObj");
        remoteObject.publicMethod();
    }

    @Test(expected = MethodNotFoundException.class)
    public void invisibleMethod() {
        remoteObject.protectedMethod();
    }

    @Test(expected = MethodInvocationException.class)
    public void invocationException() {
        remoteObject.exceptionThrowingMethod();
    }

    @Test
    public void primitives() {
        assertEquals(true, remoteObject.getBoolean(true));
        assertEquals(127, remoteObject.getByte((byte)127));
        assertEquals('c', remoteObject.getChar('c'));
        assertEquals(32767, remoteObject.getShort((short)32767));
        assertEquals(Integer.MAX_VALUE, remoteObject.getInt(Integer.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, remoteObject.getLong(Long.MAX_VALUE));
        assertEquals(1.1f, remoteObject.getFloat(1.1f), 0.001f);
        assertEquals(1.1d, remoteObject.getDouble(1.1d), 0.001d);
    }

    @Test
    public void wrappers() {
        assertEquals(true, remoteObject.getBigBoolean(true));
        assertEquals(127, remoteObject.getBigByte(Byte.MAX_VALUE).byteValue());
        assertEquals('c', remoteObject.getBigCharacter('c').charValue());
        assertEquals(32767, remoteObject.getBigShort(Short.MAX_VALUE).shortValue());
        assertEquals(Integer.MAX_VALUE, remoteObject.getBigInteger(Integer.MAX_VALUE).intValue());
        assertEquals(Long.MAX_VALUE, remoteObject.getBigLong(Long.MAX_VALUE).longValue());
        assertEquals(Float.MAX_VALUE, remoteObject.getBigFloat(Float.MAX_VALUE), 0.001f);
        assertEquals(Double.MAX_VALUE, remoteObject.getBigDouble(Double.MAX_VALUE), 0.001d);
    }

    @Test
    public void nullWrapperParameters() {
        assertNull(remoteObject.getBigBoolean(null));
        assertNull(remoteObject.getBigByte(null));
        assertNull(remoteObject.getBigCharacter(null));
        assertNull(remoteObject.getBigShort(null));
        assertNull(remoteObject.getBigInteger(null));
        assertNull(remoteObject.getBigLong(null));
        assertNull(remoteObject.getBigFloat(null));
        assertNull(remoteObject.getBigDouble(null));
    }

    @Test
    public void string() {
        assertEquals("string", remoteObject.getString("string"));
        assertEquals("", remoteObject.getString(""));
        assertNull(remoteObject.getString("null"));
        assertNull(remoteObject.getString(null));
    }

}
