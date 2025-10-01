package io.github.bakedlibs.dough.skins;

import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public final class CustomGameProfile {

    /**
     * The player name for this profile.
     * "CS-CoreLib" for historical reasons and backwards compatibility.
     */
    private static final String PLAYER_NAME = "CS-CoreLib";

    /**
     * The skin's property key.
     */
    private static final String PROPERTY_KEY = "textures";

    public static GameProfile create(@Nonnull UUID uuid, @Nullable String texture){
        GameProfile profile = new GameProfile(uuid, PLAYER_NAME);
        if(texture != null){
            profile.getProperties().put(PROPERTY_KEY, new Property(PROPERTY_KEY, texture));
        }
        return profile;
    }

    public static void apply(GameProfile profile, @Nonnull SkullMeta meta, URL skinUrl) throws NoSuchFieldException, IllegalAccessException, UnknownServerVersionException {
        // setOwnerProfile was added in 1.18, but getOwningPlayer throws a NullPointerException since 1.20.2
        if (MinecraftVersion.get().isAtLeast(MinecraftVersion.parse("1.20"))) {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(profile.getId(), PLAYER_NAME);
            PlayerTextures playerTextures = playerProfile.getTextures();
            playerTextures.setSkin(skinUrl);
            playerProfile.setTextures(playerTextures);
            meta.setOwnerProfile(playerProfile);
        } else {
            // Forces SkullMeta to properly deserialize and serialize the profile
            ReflectionUtils.setFieldValue(meta, "profile", profile);

            meta.setOwningPlayer(meta.getOwningPlayer());

            // Now override the texture again
            ReflectionUtils.setFieldValue(meta, "profile", profile);
        }

    }

    /**
     * Get the base64 encoded texture from the underline GameProfile.
     *
     * @return the base64 encoded texture.
     */
    @Nullable
    public static String getBase64Texture(GameProfile profile) {
        Property property = profile.getProperties().get(PROPERTY_KEY).stream().filter(p -> PROPERTY_KEY.equals( p.name())).findAny().orElse(null);
        if(property != null){
            return property.value();
        }
        return null;
    }
}
