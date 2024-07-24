package io.github.bakedlibs.dough.protection.modules;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

public class PreciousStonesProtectionModule implements ProtectionModule {
    private IApi api;
    private final Plugin plugin;

    public PreciousStonesProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        api = PreciousStones.API();
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (!(p instanceof Player)) {
            return false;
        }

        return switch (action) {
            case ATTACK_PLAYER -> !api.flagAppliesToPlayer((Player) p, FieldFlag.PREVENT_PVP, l);
            case INTERACT_ENTITY, ATTACK_ENTITY ->
                    !api.flagAppliesToPlayer((Player) p, FieldFlag.PREVENT_ENTITY_INTERACT, l);
            case BREAK_BLOCK -> api.canBreak((Player) p, l);
            case INTERACT_BLOCK, PLACE_BLOCK -> api.canPlace((Player) p, l);
            default -> false;
        };
    }
}
