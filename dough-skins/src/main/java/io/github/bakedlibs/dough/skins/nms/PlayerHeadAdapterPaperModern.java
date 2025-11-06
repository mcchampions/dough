package io.github.bakedlibs.dough.skins.nms;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Ref;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

class PlayerHeadAdapterPaperModern implements PlayerHeadAdapter {

    private final Constructor<? extends PlayerProfile> newCraftPlayerProfile;
    private final Method getSnapshot;
    private final Method applyTo;

    PlayerHeadAdapterPaperModern() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
        Class<?> craftPlayerProfile = Class.forName("com.destroystokyo.paper.profile.CraftPlayerProfile");
        newCraftPlayerProfile = (Constructor<? extends PlayerProfile>) ReflectionUtils.getConstructor(craftPlayerProfile, GameProfile.class);
        Class<?> craftBlockEntityState = Class.forName("org.bukkit.craftbukkit.block.CraftBlockEntityState");
        getSnapshot = craftBlockEntityState.getDeclaredMethod("getSnapshot");
        getSnapshot.setAccessible(true);
        Class<?> blockEntity = getSnapshot.getReturnType();
        applyTo = craftBlockEntityState.getDeclaredMethod("applyTo", blockEntity);
        applyTo.setAccessible(true);

    }

    @Override
    @ParametersAreNonnullByDefault
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        BlockState state = block.getState(false);
        if (state instanceof Skull) {
            Skull skull = (Skull) state;
            skull.setPlayerProfile(newCraftPlayerProfile.newInstance(profile));
            if (sendBlockUpdate) {
                skull.update(true, false);
            } else {
                Object blockEntity = getSnapshot.invoke(skull);
                applyTo.invoke(skull, blockEntity);
            }
        }
    }

}
