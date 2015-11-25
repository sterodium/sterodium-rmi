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
        assertEquals(127, remoteObject.getByte((byte) 127));
        assertEquals('c', remoteObject.getChar('c'));
        assertEquals(32767, remoteObject.getShort((short) 32767));
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
    public void primitiveArray() {
        assertEquals(true, remoteObject.getBooleanArray(new boolean[]{true})[0]);
        assertEquals(127, remoteObject.getByteArray(new byte[]{127})[0]);
        assertEquals('c', remoteObject.getCharArray(new char[]{'c'})[0]);
        assertEquals(32767, remoteObject.getShortArray(new short[]{32767})[0]);
        assertEquals(Integer.MAX_VALUE, remoteObject.getIntArray(new int[]{Integer.MAX_VALUE})[0]);
        assertEquals(Long.MAX_VALUE, remoteObject.getLongArray(new long[]{Long.MAX_VALUE})[0]);
        assertEquals(1.1f, remoteObject.getFloatArray(new float[]{1.1f})[0], 0.001f);
        assertEquals(1.1d, remoteObject.getDoubleArray(new double[]{1.1d})[0], 0.001d);
    }

    @Test
    public void primitiveWrapperArray() {
        assertEquals(true, remoteObject.getBigBooleanArray(new Boolean[]{true})[0].booleanValue());
        assertEquals(127, remoteObject.getBigByteArray(new Byte[]{127})[0].byteValue());
        assertEquals('c', remoteObject.getBigCharacterArray(new Character[]{'c'})[0].charValue());
        assertEquals(32767, remoteObject.getBigShortArray(new Short[]{32767})[0].shortValue());
        assertEquals(Integer.MAX_VALUE, remoteObject.getBigIntegerArray(new Integer[]{Integer.MAX_VALUE})[0].intValue());
        assertEquals(Long.MAX_VALUE, remoteObject.getBigLongArray(new Long[]{Long.MAX_VALUE})[0].longValue());
        assertEquals(1.1f, remoteObject.getBigFloatArray(new Float[]{1.1f})[0].floatValue(), 0.001f);
        assertEquals(1.1d, remoteObject.getBigDoubleArray(new Double[]{1.1d})[0].doubleValue(), 0.001d);
    }

    @Test
    public void string() {
        assertEquals("string", remoteObject.getString("string"));
        assertEquals("", remoteObject.getString(""));
        assertNull(remoteObject.getString("null"));
        assertNull(remoteObject.getString(null));
    }

    @Test
    public void stringArray() {
        assertEquals("string", remoteObject.getStringArray(new String[]{"string"})[0]);
        assertEquals("", remoteObject.getStringArray(new String[]{""})[0]);
        assertEquals("null", remoteObject.getStringArray(new String[]{"null"})[0]);
        assertNull(remoteObject.getStringArray(new String[]{null})[0]);
        assertNull(remoteObject.getStringArray(null));
    }

    // Regular tests without checking response
// TODO: MVO: arguments / return types:
// TODO: MVO:   array of interfaces
// TODO: MVO:   array of arrays
// TODO: MVO:   varargs (primitive, objects)
// TODO: MVO:   collection of primitives
// TODO: MVO:   types of collections (ArrayList, HashSet, HashMap?, ImmutableList?)
// TODO: MVO:   collection of objects
// TODO: MVO:   collection of arrays
// TODO: MVO:   get collection and update it remotely?
// TODO: MVO:   generics:
// TODO: MVO:       collection of super
// TODO: MVO:       collection of extends
// TODO: MVO:       collection of ?
// TODO: MVO:       collection of Object but with specific types inside

// TODO: MVO: call super class method
// TODO: MVO: call protected super class method overriden to make it public
// TODO: MVO: call chained methods:
// TODO: MVO:   regular chain
// TODO: MVO:   delegation to super class and back
// TODO: MVO:   destroy cache in cycle try method on not existing object



}
