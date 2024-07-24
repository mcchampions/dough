package io.github.bakedlibs.dough.reflection;

import java.util.HashMap;
import java.util.Map;

final class PrimitiveTypeConversion {

    private static final Map<Class<?>, Class<?>> primitiveTypes = new HashMap<>();

    static {
        primitiveTypes.put(Byte.class, Byte.TYPE);
        primitiveTypes.put(Short.class, Short.TYPE);
        primitiveTypes.put(Integer.class, Integer.TYPE);
        primitiveTypes.put(Long.class, Long.TYPE);
        primitiveTypes.put(Character.class, Character.TYPE);
        primitiveTypes.put(Float.class, Float.TYPE);
        primitiveTypes.put(Double.class, Double.TYPE);
        primitiveTypes.put(Boolean.class, Boolean.TYPE);
    }

    private PrimitiveTypeConversion() {}

    static Class<?> convert(Class<?> boxedClassType) {
        return primitiveTypes.get(boxedClassType);
    }

    static Class<?> convertIfNecessary(Class<?> boxedClassType) {
        Class<?> primitiveType = convert(boxedClassType);
        return primitiveType != null ? primitiveType : boxedClassType;
    }

}
