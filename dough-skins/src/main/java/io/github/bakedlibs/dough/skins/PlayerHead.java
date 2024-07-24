package io.github.bakedlibs.dough.skins;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;

import io.github.bakedlibs.dough.skins.nms.PlayerHeadAdapter;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;

public final class PlayerHead {
    private static final PlayerHeadAdapter adapter = PlayerHeadAdapter.get();

    private PlayerHead() {}

    /**
     * This Method will simply return the Head of the specified Player
     * 
     * @param player
     *            The Owner of your Head
     * 
     * @return A new Head Item for the specified Player
     */
    public static ItemStack getItemStack(OfflinePlayer player) {
        return getItemStack(meta -> meta.setOwningPlayer(player));
    }

    /**
     * This Method will simply return the Head of the specified Player
     * 
     * @param skin
     *            The skin of the head you want.
     * 
     * @return A new Head Item for the specified Player
     */
    public static ItemStack getItemStack(PlayerSkin skin) {
        return getItemStack(meta -> {
            try {
                skin.getProfile().apply(meta);
            } catch (NoSuchFieldException | IllegalAccessException | UnknownServerVersionException e) {
                e.printStackTrace();
            }
        });
    }

    private static ItemStack getItemStack(Consumer<SkullMeta> consumer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);
        return item;
    }

    public static void setSkin(Block block, PlayerSkin skin, boolean sendBlockUpdate) {
        if (adapter == null) {
            throw new UnsupportedOperationException("Cannot update skin texture, no adapter found");
        }

        Material material = block.getType();

        if (material != Material.PLAYER_HEAD && material != Material.PLAYER_WALL_HEAD) {
            throw new IllegalArgumentException("Cannot update a head texture. Expected a Player Head, received: " + material);
        }

        try {
            GameProfile profile = skin.getProfile();
            adapter.setGameProfile(block, profile, sendBlockUpdate);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
