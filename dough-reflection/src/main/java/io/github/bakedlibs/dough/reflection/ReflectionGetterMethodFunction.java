package io.github.bakedlibs.dough.reflection;

@FunctionalInterface
public interface ReflectionGetterMethodFunction {
    Object invoke(Object instance);
}