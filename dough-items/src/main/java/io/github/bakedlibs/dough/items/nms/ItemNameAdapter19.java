package io.github.bakedlibs.dough.items.nms;

import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionReturnableStaticMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

class ItemNameAdapter19 implements ItemNameAdapter {
    private final ReflectionReturnableStaticMethodFunction getCopy;
    private final ReflectionGetterMethodFunction getName;
    private final ReflectionGetterMethodFunction toString;

    ItemNameAdapter19() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {

        Method getCopy = ReflectionUtils.getOBCClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
        // Spigot has changed the obf again for 1.19, so we have to adapt with it.
        Method getName = ReflectionUtils.getMethodOrAlternative(ReflectionUtils.getNetMinecraftClass("world.item.ItemStack"), "getDisplayName", "I");
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
