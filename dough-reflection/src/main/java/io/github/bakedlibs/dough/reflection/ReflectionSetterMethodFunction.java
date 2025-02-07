package io.github.bakedlibs.dough.reflection;

@FunctionalInterface
public interface ReflectionSetterMethodFunction {
    void invoke(Object instance, Object param);
}