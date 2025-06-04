package io.github.bakedlibs.dough.skins.nms;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.bakedlibs.dough.reflection.ReflectionGetterMethodFunction;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import java.lang.reflect.Method;

public class PlayerHeadAdapterPaper implements PlayerHeadAdapter {
    private ReflectionGetterMethodFunction getName;
    private ReflectionGetterMethodFunction getValue;
    private ReflectionGetterMethodFunction getSignature;

    PlayerHeadAdapterPaper() {
        Method getName = ReflectionUtils.getMethod(Property.class, "getName");
        Method getValue = ReflectionUtils.getMethod(Property.class, "getValue");
        Method getSignature = ReflectionUtils.getMethod(Property.class, "getSignature");
        if (getName != null) {
            getValue.setAccessible(true);
            getName.setAccessible(true);
            getSignature.setAccessible(true);
            this.getName = ReflectionUtils.createGetterFunction(getName);
            this.getValue = ReflectionUtils.createGetterFunction(getValue);
            this.getSignature = ReflectionUtils.createGetterFunction(getSignature);
        }
    }

    @Override
    public void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) {
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
