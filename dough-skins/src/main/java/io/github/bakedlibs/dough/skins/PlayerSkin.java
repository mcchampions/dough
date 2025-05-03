package io.github.bakedlibs.dough.skins;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerSkin {
    private static final String ERROR_TOKEN = "error";

    private final CustomGameProfile profile;

    PlayerSkin(UUID uuid, String base64skinTexture, URL url) {
        this.profile = new CustomGameProfile(uuid, base64skinTexture, url);
    }

    public final CustomGameProfile getProfile() {
        return profile;
    }
    
    public static PlayerSkin fromBase64(UUID uuid, String base64skinTexture, URL url) {
        return new PlayerSkin(uuid, base64skinTexture, url);
    }

    /**
     * @deprecated use {@link #fromBase64(UUID, String, URL)}
     */
    @Deprecated
    public static PlayerSkin fromBase64(UUID uuid, String base64skinTexture) {
        String base64decode = new String(Base64.getDecoder().decode(base64skinTexture));
        JsonObject jsonObject = new JsonParser().parse(base64decode).getAsJsonObject();
        String url = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        URL skinUrl;
        try {
            skinUrl = URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new PlayerSkin(uuid, base64skinTexture, skinUrl);
    }

    public static PlayerSkin fromBase64(String base64skinTexture) {
        UUID uuid = UUID.nameUUIDFromBytes(base64skinTexture.getBytes(StandardCharsets.UTF_8));
        return fromBase64(uuid, base64skinTexture);
    }

    public static PlayerSkin fromURL(UUID uuid, String url) {
        String value = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        String base64skinTexture = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        URL skinUrl;
        try {
           skinUrl = URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return fromBase64(uuid, base64skinTexture, skinUrl);
    }

    public static PlayerSkin fromURL(String url) {
        UUID uuid = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
        return fromURL(uuid, url);
    }

    public static PlayerSkin fromHashCode(UUID uuid, String hashCode) {
        return fromURL(uuid, "http://textures.minecraft.net/texture/" + hashCode);
    }

    public static PlayerSkin fromHashCode(String hashCode) {
        UUID uuid = UUID.nameUUIDFromBytes(hashCode.getBytes(StandardCharsets.UTF_8));
        return fromHashCode(uuid, hashCode);
    }

    public static CompletableFuture<PlayerSkin> fromPlayerUUID(Plugin plugin, UUID uuid) {
        CompletableFuture<PlayerSkin> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String targetUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false";

            try (InputStreamReader reader = new InputStreamReader(new URL(targetUrl).openStream(), StandardCharsets.UTF_8)) {
                JsonElement element = JsonParser.parseReader(reader);

                if (!(element instanceof JsonNull)) {
                    JsonObject obj = element.getAsJsonObject();

                    if (obj.has(ERROR_TOKEN)) {
                        String error = obj.get(ERROR_TOKEN).getAsString();
                        future.completeExceptionally(new UnsupportedOperationException(error));
                        return;
                    }

                    JsonArray properties = obj.get("properties").getAsJsonArray();

                    for (JsonElement el : properties) {
                        if (el.isJsonObject() && el.getAsJsonObject().get("name").getAsString().equals("textures")) {
                            String base64Texture = el.getAsJsonObject().get("value").getAsString();
                            PlayerSkin playerSkin = PlayerSkin.fromBase64(uuid, base64Texture);

                            future.complete(playerSkin);
                            return;
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

}
