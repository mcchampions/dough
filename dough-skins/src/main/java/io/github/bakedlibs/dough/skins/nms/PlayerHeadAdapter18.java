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

class PlayerHeadAdapter18 implements PlayerHeadAdapter {
    private final MethodHandle newPosition;

    private final ReflectionGetterMethodFunction getHandle;
    private final MethodHandle getTileEntity;
    private final ReflectionSetterMethodFunction setGameProfile;

    PlayerHeadAdapter18() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {
        Method setGameProfile = ReflectionUtils.getNetMinecraftClass("world.level.block.entity.TileEntitySkull").getMethod("a", GameProfile.class);
        setGameProfile.setAccessible(true);
        Method getHandle = ReflectionUtils.getOBCClass("CraftWorld").getMethod("getHandle");
        getHandle.setAccessible(true);
        Class<?> blockPosition = ReflectionUtils.getNetMinecraftClass("core.BlockPosition");
        Constructor<?> newPosition = ReflectionUtils.getConstructor(blockPosition, int.class, int.class, int.class);
        newPosition.setAccessible(true);
        Method getTileEntity = ReflectionUtils.getNMSClass("level.WorldServer").getMethod("getBlockEntity", blockPosition, boolean.class);
        getTileEntity.setAccessible(true);
        this.getHandle = ReflectionUtils.createGetterFunction(getHandle);
        this.setGameProfile = ReflectionUtils.createSetterFunction(setGameProfile);
        try {
            this.newPosition = ReflectionUtils.LOOKUP.unreflectConstructor(newPosition);
            this.getTileEntity = ReflectionUtils.LOOKUP.unreflect(getTileEntity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

        setGameProfile.invoke(tileEntity, profile);

        if (sendBlockUpdate) {
            block.getState().update(true, false);
        }
    }
}
