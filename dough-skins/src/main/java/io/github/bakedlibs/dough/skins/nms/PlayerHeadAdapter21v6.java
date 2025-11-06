package io.github.bakedlibs.dough.skins.nms;

import com.mojang.authlib.GameProfile;
import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionSetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

import org.bukkit.block.Block;

class PlayerHeadAdapter21v6 implements PlayerHeadAdapter {

    private final MethodHandle newPosition;
    private final MethodHandle newResolvableProfile;

    private final ReflectionGetterMethodFunction getHandle;
    private final MethodHandle getTileEntity;
    private final ReflectionSetterMethodFunction setOwner;

    PlayerHeadAdapter21v6() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {
        Class<?> resolvableProfile = ReflectionUtils.getNetMinecraftClass("world.item.component.ResolvableProfile");
        Class<?> blockPosition = ReflectionUtils.getNetMinecraftClass("core.BlockPosition");
        Constructor<?> newResolvableProfileC = ReflectionUtils.getConstructor(resolvableProfile, GameProfile.class);
        Constructor<?> newPositionC = ReflectionUtils.getConstructor(blockPosition, int.class, int.class, int.class);

        setOwner = ReflectionUtils.createSetterFunction(
                ReflectionUtils.getNetMinecraftClass("world.level.block.entity.TileEntitySkull").getMethod("a", resolvableProfile));
        getHandle = ReflectionUtils.createGetterFunction(ReflectionUtils.getOBCClass("CraftWorld").getMethod("getHandle"));

        try {
            this.newPosition = ReflectionUtils.LOOKUP.unreflectConstructor(newPositionC);
            this.newResolvableProfile = ReflectionUtils.LOOKUP.unreflectConstructor(newResolvableProfileC);
            getTileEntity = ReflectionUtils.LOOKUP.unreflect(
                    ReflectionUtils.getNMSClass("level.WorldServer").getMethod("getBlockEntity", blockPosition));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getTileEntity(Block block) {
        Object world = getHandle.invoke(block.getWorld());

        try {
            Object position = newPosition.invoke(block.getX(), block.getY(), block.getZ());
            return getTileEntity.invoke(world, position);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) {
        Object tileEntity = getTileEntity(block);
        if (tileEntity == null) return;

        try {
            Object resolvableProfile = newResolvableProfile.invoke(profile);
            setOwner.invoke(tileEntity, resolvableProfile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (sendBlockUpdate) {
            block.getState().update(true, false);
        }
    }

}
