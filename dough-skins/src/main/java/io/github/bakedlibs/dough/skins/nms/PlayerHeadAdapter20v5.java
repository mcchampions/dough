package io.github.bakedlibs.dough.skins.nms;

import com.mojang.authlib.GameProfile;
import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionSetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import org.bukkit.block.Block;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class PlayerHeadAdapter20v5 implements PlayerHeadAdapter {
    private final MethodHandle newPosition;
    private final MethodHandle newResolvableProfile;

    private final ReflectionGetterMethodFunction getHandle;
    private final MethodHandle getTileEntity;
    private final ReflectionSetterMethodFunction setOwner;

    PlayerHeadAdapter20v5() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {
        Class<?> resolvableProfile = ReflectionUtils.getNetMinecraftClass("world.item.component.ResolvableProfile");
        Constructor<?> newResolvableProfile = ReflectionUtils.getConstructor(resolvableProfile, GameProfile.class);
        newResolvableProfile.setAccessible(true);

        Method setOwner = ReflectionUtils.getNetMinecraftClass("world.level.block.entity.TileEntitySkull").getMethod("a", resolvableProfile);
        setOwner.setAccessible(true);
        Method getHandle = ReflectionUtils.getOBCClass("CraftWorld").getMethod("getHandle");
        getHandle.setAccessible(true);

        Class<?> blockPosition = ReflectionUtils.getNetMinecraftClass("core.BlockPosition");
        Constructor<?> newPosition = ReflectionUtils.getConstructor(blockPosition, int.class, int.class, int.class);
        Method getTileEntity = ReflectionUtils.getNMSClass("level.WorldServer").getMethod("getBlockEntity", blockPosition, boolean.class);
        getTileEntity.setAccessible(true);

        newPosition.setAccessible(true);
        try {
            this.newPosition = ReflectionUtils.LOOKUP.unreflectConstructor(newPosition);
            this.getTileEntity = ReflectionUtils.LOOKUP.unreflect(getTileEntity);
            this.newResolvableProfile = ReflectionUtils.LOOKUP.unreflectConstructor(newResolvableProfile);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.getHandle = ReflectionUtils.createGetterFunction(getHandle);
        this.setOwner = ReflectionUtils.createSetterFunction(setOwner);
    }

    private Object getTileEntity(Block block) {
        Object world = getHandle.invoke(block.getWorld());

        Object position;
        try {
            position = newPosition.invoke(block.getX(), block.getY(), block.getZ());
            return getTileEntity.invoke(world, position, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) {
        Object tileEntity = getTileEntity(block);
        if (tileEntity == null) return;

        Object resolvableProfile;
        try {
            resolvableProfile = newResolvableProfile.invoke(profile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        setOwner.invoke(tileEntity, resolvableProfile);

        if (sendBlockUpdate) {
            block.getState().update(true, false);
        }
    }

}
