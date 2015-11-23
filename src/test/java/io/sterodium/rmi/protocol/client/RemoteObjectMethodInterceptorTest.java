package io.sterodium.rmi.protocol.client;

import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import io.sterodium.rmi.protocol.server.MethodInvocationException;
import io.sterodium.rmi.protocol.server.MethodNotFoundException;
import io.sterodium.rmi.protocol.server.MethodParameterException;
import io.sterodium.rmi.protocol.server.RmiException;
import io.sterodium.rmi.protocol.server.TargetObjectNotFoundException;
import io.sterodium.rmi.protocol.server.UnsupportedProtocolException;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static io.sterodium.rmi.protocol.server.RmiProtocol.*;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 03/10/2015
 *         <p/>
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteObjectMethodInterceptorTest {

    @Mock
    RemoteObjectProxyFactory proxyFactory;

    @Mock
    RemoteInvoker remoteInvoker;

    @Mock
    MethodProxy methodProxy;

    @Mock
    MethodInvocationResultDto response;

    @InjectMocks
    RemoteObjectMethodInterceptor methodInterceptor;

    @Test
    public void shouldInvokeObjectMethodDirectlyWithoutInterception() throws Throwable {
        Method method = Object.class.getMethod("equals", Object.class);
        methodInterceptor.intercept(new Object(), method, null, methodProxy);

        verify(methodProxy, times(1)).invokeSuper(any(), any(Object[].class));
    }

    @Test
    public void shouldInvokeToStringMethodDirectlyWithoutInterception() throws Throwable {
        ArrayList mock = mock(ArrayList.class);
        Method method = ArrayList.class.getMethod("toString");
        methodInterceptor.intercept(mock, method, new Object[0], methodProxy);

        verify(methodProxy, times(1)).invokeSuper(any(), any(Object[].class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldPropagateExceptionThatHappenInInvoker() throws Throwable {
        //noinspection unchecked
        when(remoteInvoker.invoke(anyString(), any(Method.class), any(Object[].class))).thenThrow(RuntimeException.class);
        methodInterceptor = new RemoteObjectMethodInterceptor(null, remoteInvoker, null);

        ForThisTest mock = mock(ForThisTest.class);
        Method method = mock.getClass().getMethod("getVoidMethod");
        methodInterceptor.intercept(mock, method, new Object[0], methodProxy);
    }

    private void mockMethodInvocationResultDto(String t1, String t) {
        when(remoteInvoker.invoke(anyString(), any(Method.class), any(Object[].class))).thenReturn(response);
        when(response.getValue()).thenReturn(t);
        when(response.getType()).thenReturn(t1);
    }

    @Test
    public void shouldReturnNullWhenMethodReturnsVoid() throws Throwable {
        mockMethodInvocationResultDto(null, null);
        Method voidMethod = ForThisTest.class.getMethod("getVoidMethodWithArg", String.class);
        Object result = methodInterceptor.intercept(null, voidMethod, new Object[0], methodProxy);

        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnNullWhenMethodReturnsVoid_DataFromInvokerIsIncorrect() throws Throwable {
        mockMethodInvocationResultDto("java.lang.String", "notNullByMistake");
        Method voidMethod = ForThisTest.class.getMethod("getVoidMethodWithArg", String.class);
        Object result = methodInterceptor.intercept(null, voidMethod, new Object[0], methodProxy);

        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnStringWhenMethodReturnString() throws Throwable {
        mockMethodInvocationResultDto("java.lang.String", "expected message");
        Method getMessageMethod = ForThisTest.class.getMethod("getStringMethod");
        Object result = methodInterceptor.intercept(null, getMessageMethod, new Object[0], methodProxy);

        assertThat(result.toString(), is("expected message"));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsInt() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Integer", "1");
        Method method = ForThisTest.class.getMethod("getIntegerMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Integer.class));
        assertThat((Integer) result, is(1));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsDouble() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Double", "1.0");
        Method method = ForThisTest.class.getMethod("getDoubleMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Double.class));
        assertThat((Double) result, is(1.0));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsFloat() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Float", "1.0f");
        ForThisTest mock = mock(ForThisTest.class);
        Method method = mock.getClass().getMethod("getFloatMethod");
        Object result = methodInterceptor.intercept(mock, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Float.class));
        assertThat((Float) result, is(1.0f));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsLong() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Long", "1314914");
        Method method = ForThisTest.class.getMethod("getLongMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Long.class));
        assertThat((Long) result, is(1314914L));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsShort() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Short", "111");
        Method method = ForThisTest.class.getMethod("getShortMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Short.class));
        assertThat(((Short) result).intValue(), is(111));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsByte() throws Throwable {
        mockMethodInvocationResultDto("java.lang.Byte", "77");
        Method method = ForThisTest.class.getMethod("getByteMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Class doesn't match expected", result.getClass().equals(Byte.class));
        assertThat(((Byte) result).intValue(), is(77));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void shouldReturnStringWhenMethodReturnsNonPrimitiveType() throws Throwable {
        mockMethodInvocationResultDto("java.lang.StringBuilder", "someWidgetId");
        StringBuilder sb = new StringBuilder();
        when(proxyFactory.create(StringBuilder.class, "someWidgetId")).thenReturn(sb);
        Method method = ForThisTest.class.getMethod("getStringBuilderMethod");
        Object result = methodInterceptor.intercept(null, method, new Object[0], methodProxy);

        assertTrue("Response object is not expected one", result.equals(sb));
        verify(remoteInvoker, times(1)).invoke(anyString(), any(Method.class), any(Object[].class));
    }

    @Test
    public void checkErrorCodes() {

        // no error
        methodInterceptor.checkErrorCodes(response);

        // supported error code
        response = mock(MethodInvocationResultDto.class, RETURNS_DEEP_STUBS);
        assertErrorCode(ERROR_CODE_INVALID_JSON, UnsupportedProtocolException.class);
        assertErrorCode(ERROR_CODE_INVALID_REQUEST, UnsupportedProtocolException.class);
        assertErrorCode(ERROR_CODE_METHOD_NOT_FOUND, MethodNotFoundException.class);
        assertErrorCode(ERROR_CODE_INVALID_PARAMS, MethodParameterException.class);
        assertErrorCode(ERROR_CODE_JSONRPC_ERROR, UnsupportedProtocolException.class);
        assertErrorCode(ERROR_CODE_SERVER_ERROR, MethodInvocationException.class);
        assertErrorCode(ERROR_CODE_OBJECT_NOT_FOUND, TargetObjectNotFoundException.class);

        // unknown error code
        assertErrorCode(12345, RmiException.class);
    }

    private void assertErrorCode(int errorCode, Class<?> expectedException) {
        when(response.getError().getCode()).thenReturn(errorCode);
        try {
            methodInterceptor.checkErrorCodes(response);
            fail();
        } catch (Exception e) {
            // OK
            assertEquals(expectedException, e.getClass());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class ForThisTest {

        public String getStringMethod() {
            return null;
        }

        public void getVoidMethod() {
        }

        public void getVoidMethodWithArg(String s) {
        }

        public Short getShortMethod() {
            return null;
        }

        public Integer getIntegerMethod() {
            return null;
        }

        public Long getLongMethod() {
            return null;
        }

        public Float getFloatMethod() {
            return null;
        }

        public Double getDoubleMethod() {
            return null;
        }

        public Byte getByteMethod() {
            return null;
        }

        public Boolean getBooleanMethod() {
            return true;
        }

        public Character getCharactedMethod() {
            return null;
        }

        public StringBuilder getStringBuilderMethod() {
            return null;
        }
    }
}
