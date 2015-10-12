package io.sterodium.rmi.protocol.server;

import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 06/10/2015
 *         <p/>
 */
@RunWith(MockitoJUnitRunner.class)
public class MarshallerTest {

    Marshaller marshaller;

    @Mock
    ObjectLocator objectLocator;

    @Before
    public void setUp() {
        marshaller = new Marshaller(objectLocator);

        when(objectLocator.put(anyObject())).thenReturn("widgetId");
    }

    @Test
    public void shouldHaveNullWhenReturnTypeIsVoid() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(null, Void.class);

        assertThat(resultDto.getType(), nullValue());
        assertThat(resultDto.getValue(), nullValue());
    }

    @Test
    public void shouldHaveNullWhenReturnTypeIsVoidPrimitive() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(null, void.class);

        assertThat(resultDto.getType(), nullValue());
        assertThat(resultDto.getValue(), nullValue());
    }

    @Test
    public void shouldHaveProperReturnTypeWhenValueIsNull() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(null, String.class);

        assertThat(resultDto.getType(), is("java.lang.String"));
        assertThat(resultDto.getValue(), nullValue());
    }

    @Test
    public void shouldReturnPrimitiveObjectValueAndTypeString() {
        MethodInvocationResultDto resultDto = marshaller.toResponse("expectedValue", String.class);

        assertThat(resultDto.getType(), is("java.lang.String"));
        assertThat(resultDto.getValue(), is("expectedValue"));
    }

    @Test
    public void shouldReturnPrimitiveObjectValueAndTypeInteger() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(777, int.class);

        assertThat(resultDto.getType(), is("java.lang.Integer"));
        assertThat(resultDto.getValue(), is("777"));
    }

    @Test
    public void shouldReturnWidgetIdAndTypeForComplexObjects() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(new ArrayList<>(), ArrayList.class);

        assertThat(resultDto.getType(), is("java.util.ArrayList"));
        assertThat(resultDto.getValue(), is("widgetId"));
    }

    @Test
    public void shouldReturnWidgetIdAndTypeForComplexObjects_2() {
        MethodInvocationResultDto resultDto = marshaller.toResponse(new Thread(), Thread.class);

        assertThat(resultDto.getType(), is("java.lang.Thread"));
        assertThat(resultDto.getValue(), is("widgetId"));
    }

}
