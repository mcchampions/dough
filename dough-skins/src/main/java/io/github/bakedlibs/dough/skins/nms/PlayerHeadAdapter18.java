package io.github.bakedlibs.dough.skins.nms;

import com.mojang.authlib.GameProfile;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import org.bukkit.block.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PlayerHeadAdapter18 implements PlayerHeadAdapter {
    private final Constructor<?> newPosition;

    private final Method getHandle;
    private final Method getTileEntity;
    private final Method setGameProfile;

    PlayerHeadAdapter18() throws NoSuchMethodException, SecurityException, ClassNotFoundException, UnknownServerVersionException {
        setGameProfile = ReflectionUtils.getNetMinecraftClass("world.level.block.entity.TileEntitySkull").getMethod("a", GameProfile.class);
        setGameProfile.setAccessible(true);
        getHandle = ReflectionUtils.getOBCClass("CraftWorld").getMethod("getHandle");
        getHandle.setAccessible(true);
        Class<?> blockPosition = ReflectionUtils.getNetMinecraftClass("core.BlockPosition");
        newPosition = ReflectionUtils.getConstructor(blockPosition, int.class, int.class, int.class);
        newPosition.setAccessible(true);
        getTileEntity = ReflectionUtils.getNMSClass("level.WorldServer").getMethod("getBlockEntity", blockPosition, boolean.class);
        getTileEntity.setAccessible(true);
    }

    private Object getTileEntity(Block block) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object world = getHandle.invoke(block.getWorld());

        Object position = newPosition.newInstance(block.getX(), block.getY(), block.getZ());
        return getTileEntity.invoke(world, position, true);
    }

    @Override
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object tileEntity = getTileEntity(block);
        if (tileEntity == null) return;

        setGameProfile.invoke(tileEntity, profile);

        if (sendBlockUpdate) {
            block.getState().update(true, false);
        }
    }
}
