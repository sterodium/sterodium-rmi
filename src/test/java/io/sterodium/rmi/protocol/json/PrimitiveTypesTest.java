package io.sterodium.rmi.protocol.json;

import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23.11.2015
 */
public class PrimitiveTypesTest {

    @Test
    public void isPrimitive() {
        assertTrue(PrimitiveTypes.isPrimitive(void.class));
        assertTrue(PrimitiveTypes.isPrimitive(Void.class));

        // testing all primitive and wrapper types
        Set<Class<?>> primitiveTypes = newHashSet(Primitives.allPrimitiveTypes());
        primitiveTypes.addAll(Primitives.allWrapperTypes());
        for (Class<?> primitiveType : primitiveTypes) {
            assertTrue(primitiveType + " should be primitive", PrimitiveTypes.isPrimitive(primitiveType));
        }
    }

    @Test
    public void isVoid() {
        assertTrue(PrimitiveTypes.isVoid(void.class));
        assertTrue(PrimitiveTypes.isVoid(Void.class));
        assertFalse(PrimitiveTypes.isVoid(Object.class));
    }

    @Test
    public void isCharacter() {
        assertTrue(PrimitiveTypes.isCharacter(char.class));
        assertTrue(PrimitiveTypes.isCharacter(Character.class));
        assertFalse(PrimitiveTypes.isCharacter(Object.class));
    }
}