package io.github.bakedlibs.dough.items.nms;

import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionReturnableStaticMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

class ItemNameAdapter20v5 implements ItemNameAdapter {
    private final ReflectionReturnableStaticMethodFunction getCopy;
    private final ReflectionGetterMethodFunction getName;
    private final ReflectionGetterMethodFunction toString;

    ItemNameAdapter20v5() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {
        super();

        Method getCopy = ReflectionUtils.getOBCClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
        Method getName = ReflectionUtils.getMethodOrAlternative(ReflectionUtils.getNetMinecraftClass("world.item.ItemStack"), "getDisplayName", "G");
        Method toString = ReflectionUtils.getMethod(ReflectionUtils.getNetMinecraftClass("network.chat.IChatBaseComponent"), "getString");
        getCopy.setAccessible(true);
        getName.setAccessible(true);
        toString.setAccessible(true);
        this.getCopy = ReflectionUtils.createReturnableStaticFunction(getCopy);
        this.getName = ReflectionUtils.createGetterFunction(getName);
        this.toString = ReflectionUtils.createGetterFunction(toString);
    }

    @Override
    public String getName(ItemStack item) {
        Object instance = getCopy.invoke(item);
        return (String) toString.invoke(getName.invoke(instance));
    }

}
