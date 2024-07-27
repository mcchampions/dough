package io.github.bakedlibs.dough.skins.nms;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

public class PlayerHeadAdapterPaper implements PlayerHeadAdapter {
    private final Method getName;
    private final Method getValue;
    private final Method getSignature;

    PlayerHeadAdapterPaper() {
        getName = ReflectionUtils.getMethod(Property.class, "getName");
        getValue = ReflectionUtils.getMethod(Property.class, "getValue");
        getSignature = ReflectionUtils.getMethod(Property.class, "getSignature");
    }

    @Override
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) throws InvocationTargetException, IllegalAccessException {
        BlockState state = block.getState(false);
        if (!(state instanceof Skull skull)) return;

        Property property = profile.getProperties().get("textures").iterator().next();

        PlayerProfile paperPlayerProfile = Bukkit.createProfile(profile.getId(), profile.getName());

        // Old authlib check
        if (getName != null) {
            paperPlayerProfile.setProperty(new ProfileProperty((String) getName.invoke(property), (String) getValue.invoke(property), (String) getSignature.invoke(property)));
        } else {
            paperPlayerProfile.setProperty(new ProfileProperty(property.name(), property.value(), property.signature()));
        }

        skull.setPlayerProfile(paperPlayerProfile);

        if (sendBlockUpdate) {
            skull.update(true, false);
        }
    }
}
