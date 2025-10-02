package io.github.bakedlibs.dough.skins;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.PropertyMap;
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

    public static GameProfile create(@Nonnull UUID uuid, @Nullable String texture) {

        if (texture != null) {
            if (gameProfileConstructor == null) {
                GameProfile profile = new GameProfile(uuid, PLAYER_NAME);
                PropertyMap map = getProperties(profile);
                map.put(PROPERTY_KEY, new Property(PROPERTY_KEY, texture));
                return profile;
            } else {
                Multimap<String, Property> propertyMultimap = LinkedHashMultimap.create();
                propertyMultimap.put(PROPERTY_KEY, new Property(PROPERTY_KEY, texture));
                try {
                    PropertyMap map1 = propertyMapConstructor.newInstance(propertyMultimap);
                    return gameProfileConstructor.newInstance(uuid, PLAYER_NAME, map1);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return new GameProfile(uuid, PLAYER_NAME);
        }
    }

    private static final Field propertiesField;
    private static final Field idField;
    private static final Field nameField;
    private static final Constructor<PropertyMap> propertyMapConstructor;
    private static final Constructor<GameProfile> gameProfileConstructor;

    static {
        try {
            propertiesField = GameProfile.class.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            idField = GameProfile.class.getDeclaredField("id");
            idField.setAccessible(true);
            nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);
            propertyMapConstructor = (Constructor<PropertyMap>) PropertyMap.class.getConstructors()[0];
            propertyMapConstructor.setAccessible(true);
            gameProfileConstructor = (Constructor<GameProfile>) Arrays.stream(GameProfile.class.getDeclaredConstructors()).filter(c -> c.getParameterCount() == 3).findAny().orElse(null);
            if (gameProfileConstructor != null) {
                gameProfileConstructor.setAccessible(true);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static PropertyMap getProperties(GameProfile profile) {
        if (profile == null) return null;
        try {
            if (!MinecraftVersion.get().isAtLeast(1, 21, 9)) {
                return profile.getProperties();
            }
        } catch (NoSuchMethodError | UnknownServerVersionException ignored) {

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            return (PropertyMap) propertiesField.get(profile);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static UUID getId(GameProfile profile) {
        if (profile == null) return null;
        try {
            if (!MinecraftVersion.get().isAtLeast(1, 21, 9)) {
                return profile.getId();
            }
        } catch (NoSuchMethodError | UnknownServerVersionException ignored) {

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            return (UUID) idField.get(profile);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getName(GameProfile profile) {
        if (profile == null) return null;
        try {
            if (!MinecraftVersion.get().isAtLeast(1, 21, 9)) {
                return profile.getName();
            }
        } catch (NoSuchMethodError | UnknownServerVersionException ignored) {

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            return (String) nameField.get(profile);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static void apply(GameProfile profile, @Nonnull SkullMeta meta, URL skinUrl) throws NoSuchFieldException, IllegalAccessException, UnknownServerVersionException {
        // setOwnerProfile was added in 1.18, but getOwningPlayer throws a NullPointerException since 1.20.2
        if (MinecraftVersion.get().isAtLeast(MinecraftVersion.parse("1.20"))) {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(getId(profile), PLAYER_NAME);
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
        Property property = getProperties(profile).get(PROPERTY_KEY).stream().filter(p -> PROPERTY_KEY.equals(p.name())).findAny().orElse(null);
        if (property != null) {
            return property.value();
        }
        return null;
    }
}
