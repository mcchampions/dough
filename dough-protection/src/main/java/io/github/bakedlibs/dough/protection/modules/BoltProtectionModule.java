package io.github.bakedlibs.dough.protection.modules;

import io.github.bakedlibs.dough.protection.ActionType;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.popcraft.bolt.BoltAPI;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Permission;

public class BoltProtectionModule implements ProtectionModule {
    private BoltAPI bolt;
    private final Plugin plugin;

    public BoltProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public void load() {
        bolt = this.plugin.getServer().getServicesManager().load(BoltAPI.class);
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (action.getType() != ActionType.BLOCK) {
            return true;
        }

        String permission = boltPermission(action);
        if (permission == null) {
            return true;
        }
        Protection protection = this.bolt.findProtection(l.getBlock());
        return this.bolt.canAccess(protection, p.getUniqueId(), permission);
    }

    private static String boltPermission(Interaction interaction) {
        return switch (interaction) {
            case BREAK_BLOCK, ATTACK_PLAYER, ATTACK_ENTITY -> Permission.DESTROY;
            case PLACE_BLOCK, INTERACT_BLOCK, INTERACT_ENTITY -> Permission.INTERACT;
        };
    }
}
