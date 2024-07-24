package io.github.bakedlibs.dough.protection.modules;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;

import me.angeschossen.lands.api.land.LandWorld;

public class LandsProtectionModule implements ProtectionModule {
    private LandsIntegration landsIntegration;
    private final Plugin lands;
    private final Plugin plugin;

    public LandsProtectionModule(Plugin lands, Plugin plugin) {
        this.lands = lands;
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return lands;
    }

    @Override
    public void load() {
        this.landsIntegration = LandsIntegration.of(plugin);
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        LandWorld landWorld = landsIntegration.getWorld(l.getWorld()); // landWorld is used for permission checks, since flags can be toggled in wilderness as well
        if (landWorld == null) {
            return true; // this is not a claim world
        }

        LandPlayer landPlayer = landsIntegration.getLandPlayer(p.getUniqueId()); // null if the player is offline
        if (landPlayer == null) { // if the player is offline, check against UUID without bypass permissions
            return landWorld.hasRoleFlag(p.getUniqueId(), l, convert(action));
        } else { // if online, check against online player with bypass permissions
            return landWorld.hasRoleFlag(landPlayer, l, convert(action), null, false); // we don't know the block material and don't want to send a denied message
        }
    }

    private static RoleFlag convert(Interaction protectableAction) {
        return switch (protectableAction) {
            case PLACE_BLOCK -> Flags.BLOCK_PLACE;
            case BREAK_BLOCK -> Flags.BLOCK_BREAK;
            case INTERACT_BLOCK -> Flags.INTERACT_CONTAINER;
            case ATTACK_PLAYER -> Flags.ATTACK_PLAYER;
            case ATTACK_ENTITY -> Flags.ATTACK_ANIMAL;
            default -> Flags.INTERACT_VILLAGER;
        };
    }
}