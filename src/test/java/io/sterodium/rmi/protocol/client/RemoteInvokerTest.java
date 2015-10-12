package io.sterodium.rmi.protocol.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 02/10/2015
 *         <p/>
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteInvokerTest {

    @Mock
    RestClient restClient;
    RemoteInvoker remoteInvoker;

    @Before
    public void setUp() throws IOException {
        remoteInvoker = new RemoteInvoker(restClient);
        when(restClient.invoke(anyString(), any(MethodInvocationDto.class))).thenReturn("{}");
    }

    @Test
    public void invokeWithVoidMethod() throws NoSuchMethodException, IOException, ClassNotFoundException {
        Method voidMethod = ClassForTest.class.getMethod("voidMethod");
        MethodInvocationResultDto resultDto = remoteInvoker.invoke("someId", voidMethod, new Object[0]);

        assertThat(resultDto.getValue(), nullValue());
        assertThat(resultDto.getType(), nullValue());

        MethodInvocationDto value = captureRestClientArgument();

        assertThat(value.getMethod(), is("voidMethod"));
        assertThat(value.getArgumentClasses().length, is(0));
        assertThat(value.getArguments().length, is(0));
    }

    @Test
    public void invokeWithVoidMethodWithArgument() throws NoSuchMethodException, IOException, ClassNotFoundException {
        Method voidMethod = ClassForTest.class.getMethod("voidMethodWithArgument", int.class);
        Object[] arguments = new Object[]{1};
        MethodInvocationResultDto resultDto = remoteInvoker.invoke("someId", voidMethod, arguments);

        assertThat(resultDto.getValue(), nullValue());
        assertThat(resultDto.getType(), nullValue());

        MethodInvocationDto value = captureRestClientArgument();

        assertThat(value.getMethod(), is("voidMethodWithArgument"));
        assertThat(value.getArgumentClasses().length, is(1));
        assertThat(value.getArguments().length, is(1));
    }

    @Test
    public void invokeWithVoidMethodWithArguments() throws NoSuchMethodException, IOException, ClassNotFoundException {
        Method voidMethod = ClassForTest.class.getMethod("voidMethodWithArguments", int.class, List.class);
        Object[] arguments = new Object[]{1, new ArrayList<>()};
        MethodInvocationResultDto resultDto = remoteInvoker.invoke("someId", voidMethod, arguments);

        assertThat(resultDto.getValue(), nullValue());
        assertThat(resultDto.getType(), nullValue());

        MethodInvocationDto value = captureRestClientArgument();

        assertThat(value.getMethod(), is("voidMethodWithArguments"));
        assertThat(value.getArgumentClasses().length, is(2));
        assertThat(value.getArguments().length, is(2));
    }

    @Test
    public void invokeWithMethodWithArgument() throws NoSuchMethodException, IOException, ClassNotFoundException {
        when(restClient.invoke(anyString(), any(MethodInvocationDto.class))).thenReturn("{\"value\":\"1\",\"type\":\"int\"}");

        Method voidMethod = ClassForTest.class.getMethod("methodWithArgument", int.class);
        Object[] arguments = new Object[]{1};
        MethodInvocationResultDto resultDto = remoteInvoker.invoke("someId", voidMethod, arguments);

        assertThat(resultDto.getValue(), is("1"));
        assertThat(resultDto.getType(), is("int"));

        MethodInvocationDto value = captureRestClientArgument();

        assertThat(value.getMethod(), is("methodWithArgument"));
        assertThat(value.getArgumentClasses().length, is(1));
        assertThat(value.getArguments().length, is(1));
    }

    @Test
    public void invokeWithMethodWithArgumentAndListReturnType() throws NoSuchMethodException, IOException, ClassNotFoundException {
        when(restClient.invoke(anyString(), any(MethodInvocationDto.class))).thenReturn("{\"value\":\"valueForList\",\"type\":\"java.util.List\"}");

        Method voidMethod = ClassForTest.class.getMethod("methodWithArgumentAndListReturnType", int.class);
        Object[] arguments = new Object[]{1};
        MethodInvocationResultDto resultDto = remoteInvoker.invoke("someId", voidMethod, arguments);

        assertThat(resultDto.getValue(), is("valueForList"));
        assertThat(resultDto.getType(), is("java.util.List"));

        MethodInvocationDto value = captureRestClientArgument();

        assertThat(value.getMethod(), is("methodWithArgumentAndListReturnType"));
        assertThat(value.getArgumentClasses().length, is(1));
        assertThat(value.getArguments().length, is(1));
    }

    private MethodInvocationDto captureRestClientArgument() throws IOException {
        ArgumentCaptor<MethodInvocationDto> argumentCaptor = ArgumentCaptor.forClass(MethodInvocationDto.class);
        verify(restClient).invoke(anyString(), argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    @Test
    public void verifyMethodInvocationIsConstructedCorrectly() {
        MethodInvocationDto dto = remoteInvoker.getMethodInvocation("method", new Class[]{String.class}, new Object[]{"string"});
        assertEquals("method", dto.getMethod());
        assertThat(dto.getArgumentClasses(), CoreMatchers.equalTo(new String[]{"java.lang.String"}));
        assertThat(dto.getArguments(), CoreMatchers.equalTo(new String[]{"string"}));
    }

    @Test
    public void verifyArgumentSerializationContract() {
        // arguments
        String stringValue = "http://localhost:80/#folder\\file.png";

        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("key1", 1);

        Map<String, Object> map2 = Maps.newHashMap();
        map2.put("key2", "2");

        // serialized arguments
        assertArgument("1", 1);
        assertArgument(stringValue, stringValue);
        assertArgument("[1,2,3]", new int[]{1, 2, 3});
        assertArgument("[1,2,3]", Lists.newArrayList(1, 2, 3));
        assertArgument("{\"key1\":1}", map1);
        assertArgument("{\"key2\":\"2\"}", map2);
        assertArgument("{\"value\":\"string\"}", new MyDto("string"));
    }

    private void assertArgument(String expectedArgumentValue, Object argument) {
        MethodInvocationDto dto = remoteInvoker.getMethodInvocation("", new Class[]{Object.class}, new Object[]{argument});
        assertThat(dto.getArguments(), CoreMatchers.equalTo(new String[]{expectedArgumentValue}));
    }

    private static class MyDto {

        @SuppressWarnings("UnusedDeclaration")
        private String value;

        public MyDto(String value) {
            this.value = value;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class ClassForTest {
        public void voidMethod() {
        }

        public void voidMethodWithArgument(int i) {
        }

        public void voidMethodWithArguments(int i, List<String> list) {
        }

        public int methodWithArgument(int i) {
            return i;
        }

        public List<String> methodWithArgumentAndListReturnType(int i) {
            return null;
        }

    }

}
