package io.github.bakedlibs.dough.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.invoke.*;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import io.papermc.lib.PaperLib;

/**
 * This class provides some useful static methods to perform reflection.
 *
 * @author TheBusyBiscuit
 */
@SuppressWarnings("java:S3011")
public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    private static String versionSpecificPackage;

    /**
     * This method returns the version-specific package name for NMS calls.
     * <br>
     * If the minecraft version check is enabled, then this will return an empty
     * {@link String} for Minecraft versions 1.17 or later.
     *
     * @param checkMinecraftVersion Whether the {@link MinecraftVersion} should be checked
     * @return The version-specific package name
     * @throws UnknownServerVersionException If the {@link MinecraftVersion} could not be determined successfully
     */
    private static String getVersionSpecificPackage(boolean checkMinecraftVersion) throws UnknownServerVersionException {
        if (checkMinecraftVersion) {
            MinecraftVersion version = MinecraftVersion.get();

            if (version.isAtLeast(1, 18)) {
                return "";
            }
        }

        if (versionSpecificPackage == null) {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();

            // Paper are no longer relocating CB to live under the version in the package name
            // This means org.bukkit.craftbukkit.v1_20_R1.CraftWorld is now org.bukkit.craftbukkit.CraftWorld
            // So we check that it is Paper and does NOT have the _v1 in the package name and just return an empty string
            if (PaperLib.isPaper() && !packageName.contains(".v1_")) {
                return (versionSpecificPackage = "");
            }

            versionSpecificPackage = packageName.substring(packageName.lastIndexOf('.') + 1) + '.';
        }

        return versionSpecificPackage;
    }

    /**
     * Returns a certain Method in the specified Class
     *
     * @param c      The Class in which the Method is in
     * @param method The Method you are looking for
     * @return The found Method
     */
    public static Method getMethod(Class<?> c, String method) {
        for (Method m : c.getMethods()) {
            if (m.getName().equals(method)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Returns a certain Method in the specified Class
     *
     * @param c           The Class in which the Method is in
     * @param method      The Method you are looking for
     * @param alternative The alternative Method you are looking for
     * @return The found Method
     */
    public static Method getMethodOrAlternative(Class<?> c, String method, String alternative) {
        Method mainMethod = getMethod(c, method);
        return mainMethod != null ? mainMethod : getMethod(c, alternative);
    }

    /**
     * Returns the Method with certain Parameters
     *
     * @param c          The Class in which the Method is in
     * @param method     The Method you are looking for
     * @param paramTypes The Types of the Parameters
     * @return The found Method
     */
    public static Method getMethod(Class<?> c, String method, Class<?>... paramTypes) {
        Class<?>[] expectParamTypes = toPrimitiveTypeArray(paramTypes);
        for (Method m : c.getMethods()) {
            Class<?>[] methodParameters = toPrimitiveTypeArray(m.getParameterTypes());

            if ((m.getName().equals(method)) && (equalsTypeArray(methodParameters, expectParamTypes))) {
                return m;
            }
        }

        return null;
    }

    /**
     * Returns the Field of a Class
     *
     * @param c     The Class conating this Field
     * @param field The name of the Field you are looking for
     * @return The found Field
     * @throws NoSuchFieldException If the field could not be found.
     */
    public static Field getField(Class<?> c, String field) throws NoSuchFieldException {
        return c.getDeclaredField(field);
    }

    /**
     * Modifies a Field in an Object
     *
     * @param <T>    The type of the specified field
     * @param object The Object containing the Field
     * @param c      The Class in which we are looking for this field
     * @param field  The Name of that Field
     * @param value  The Value for that Field
     * @throws NoSuchFieldException   If the field could not be found.
     * @throws IllegalAccessException If the field could not be modified.
     */
    public static <T> void setFieldValue(T object, Class<?> c, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = getField(c, field);
        f.setAccessible(true);
        f.set(object, value);
    }

    /**
     * Modifies a Field in an Object
     *
     * @param <T>    The type of the specified field
     * @param object The Object containing the Field
     * @param field  The Name of that Field
     * @param value  The Value for that Field
     * @throws NoSuchFieldException   If the field could not be found.
     * @throws IllegalAccessException If the field could not be modified.
     */
    public static <T> void setFieldValue(T object, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        setFieldValue(object, object.getClass(), field, value);
    }

    /**
     * Returns the Value of a Field in an Object
     *
     * @param object The Object containing the Field
     * @param field  The Name of that Field
     * @return The Value of a Field
     * @throws NoSuchFieldException   If the field could not be found.
     * @throws IllegalAccessException If the field could not be queried.
     */
    public static <T> T getFieldValue(Object object, Class<T> fieldType, String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = getField(object.getClass(), field);
        f.setAccessible(true);
        return fieldType.cast(f.get(object));
    }

    /**
     * Converts the Classes to a Primitive Type Array
     * in order to be used as paramaters
     *
     * @param classes The Types you want to convert
     * @return An Array of primitive Types
     */
    public static Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
        int size = classes.length;
        Class<?>[] types = new Class[size];

        for (int i = 0; i < size; i++) {
            types[i] = PrimitiveTypeConversion.convertIfNecessary(classes[i]);
        }

        return types;
    }

    /**
     * Returns the Constructor of a Class with the specified Parameters
     *
     * @param <T>        The Type argument for the class of this constructor
     * @param c          The Class containing the Constructor
     * @param paramTypes The Parameters for that Constructor
     * @return The Constructor for that Class
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> c, Class<?>... paramTypes) {
        Class<?>[] expectedParamTypes = toPrimitiveTypeArray(paramTypes);

        for (Constructor<?> constructor : c.getConstructors()) {
            Class<?>[] constructorTypes = toPrimitiveTypeArray(constructor.getParameterTypes());

            if (equalsTypeArray(constructorTypes, expectedParamTypes)) {
                return (Constructor<T>) constructor;
            }
        }

        return null;
    }

    /**
     * Returns an NMS Class inside a Class
     *
     * @param name    The Name of the Class your Inner class is located in
     * @param subname The Name of the inner Class you are looking for
     * @return The Class in your specified Class
     * @throws ClassNotFoundException        If the class could not be found.
     * @throws UnknownServerVersionException If the {@link MinecraftVersion} was unable to be determined
     */
    public static Class<?> getInnerNMSClass(String name, String subname) throws ClassNotFoundException, UnknownServerVersionException {
        return getNMSClass(name + '$' + subname);
    }

    /**
     * Returns a `net.minecraft` class via Reflection.
     *
     * @param name The class name of which to fetch
     * @return The `net.minecraft` class.
     * @throws ClassNotFoundException        If the class does not exist
     * @throws UnknownServerVersionException If the {@link MinecraftVersion} was unable to be determined
     */
    public static Class<?> getNetMinecraftClass(String name) throws ClassNotFoundException, UnknownServerVersionException {
        return Class.forName("net.minecraft." + getVersionSpecificPackage(true) + name);
    }

    /**
     * Returns an NMS Class via Reflection
     *
     * @param name The Name of the Class you are looking for
     * @return The Class in that Package
     * @throws ClassNotFoundException        If the class could not be found.
     * @throws UnknownServerVersionException If the {@link MinecraftVersion} was not able to be determined.
     */
    public static Class<?> getNMSClass(String name) throws ClassNotFoundException, UnknownServerVersionException {
        return Class.forName("net.minecraft.server." + getVersionSpecificPackage(true) + name);
    }

    /**
     * Returns an OBC Class inside a Class
     *
     * @param name    The Name of the Class your Inner class is located in
     * @param subname The Name of the inner Class you are looking for
     * @return The Class in your specified Class
     * @throws ClassNotFoundException If the class could not be found.
     */
    public static Class<?> getInnerOBCClass(String name, String subname) throws ClassNotFoundException {
        return getOBCClass(name + '$' + subname);
    }

    /**
     * Returns an OBC Class via Reflection
     *
     * @param name The Name of the Class you are looking for
     * @return The Class in that Package
     * @throws ClassNotFoundException If the class could not be found.
     */
    public static Class<?> getOBCClass(String name) throws ClassNotFoundException {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersionSpecificPackage(false) + name);
        } catch (UnknownServerVersionException e) {
            throw new IllegalStateException("No version check should be performed here.", e);
        }
    }

    /**
     * Compares multiple Type Arrays
     *
     * @param a The first Array for comparison
     * @param b All following Array you want to compare
     * @return Whether they equal each other
     */
    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if ((!a[i].equals(b[i])) && (!a[i].isAssignableFrom(b[i]))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns all Enum Constants in an Enum
     *
     * @param <T> The Type argument of the enum we are querying
     * @param c   The Enum you are targeting
     * @return An ArrayList of all Enum Constants in that Enum
     */
    public static <T extends Enum<T>> List<T> getEnumConstants(Class<T> c) {
        return Arrays.asList(c.getEnumConstants());
    }

    /**
     * Returns a specific Enum Constant in an Enum
     *
     * @param <T>  The Type argument of the enum we are querying
     * @param c    The Enum you are targeting
     * @param name The Name of the Constant you are targeting
     * @return The found Enum Constant
     */
    public static <T extends Enum<T>> T getEnumConstant(Class<T> c, String name) {
        for (T field : c.getEnumConstants()) {
            if (field.toString().equals(name)) {
                return field;
            }
        }

        return null;
    }

    public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static final MethodType GETTER_METHOD_TYPE = MethodType.methodType(Object.class, Object.class);

    public static final MethodType SETTER_METHOD_TYPE = MethodType.methodType(void.class, Object.class, Object.class);

    public static ReflectionGetterMethodFunction createGetterFunction(Method method) {
        try {
            MethodHandle methodHandle = LOOKUP.unreflect(method);
            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "invoke",
                    MethodType.methodType(ReflectionGetterMethodFunction.class),
                    GETTER_METHOD_TYPE,
                    methodHandle,
                    methodHandle.type()
            );
            return (ReflectionGetterMethodFunction) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static ReflectionSetterMethodFunction createSetterFunction(Method method) {
        try {
            MethodHandle methodHandle = LOOKUP.unreflect(method);
            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "invoke",
                    MethodType.methodType(ReflectionSetterMethodFunction.class),
                    SETTER_METHOD_TYPE,
                    methodHandle,
                    methodHandle.type()
            );
            return (ReflectionSetterMethodFunction) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static ReflectionReturnableStaticMethodFunction createReturnableStaticFunction(Method method) {
        try {
            MethodHandle methodHandle = LOOKUP.unreflect(method);
            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "invoke",
                    MethodType.methodType(ReflectionReturnableStaticMethodFunction.class),
                    GETTER_METHOD_TYPE,
                    methodHandle,
                    methodHandle.type()
            );
            return (ReflectionReturnableStaticMethodFunction) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
