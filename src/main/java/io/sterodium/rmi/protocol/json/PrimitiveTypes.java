package io.sterodium.rmi.protocol.json;

import com.google.common.primitives.Primitives;

/**
 * Class provides helper methods for work with primitives and wrapper types.
 *
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23.11.2015
 */
public final class PrimitiveTypes {

    private PrimitiveTypes() {
        // disabling object construction
    }

    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || Primitives.isWrapperType(type);
    }

    public static boolean isVoid(Class<?> returnType) {
        return void.class.equals(returnType) || Void.class.equals(returnType);
    }

    public static boolean isCharacter(Class type) {
        return char.class.equals(type) || Character.class.equals(type);
    }

}
