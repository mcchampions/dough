package io.github.bakedlibs.dough.protection.modules;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;

public class RedProtectProtectionModule implements ProtectionModule {
    private RedProtectAPI api;
    private final Plugin plugin;

    public RedProtectProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        api = RedProtect.get().getAPI();
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        Region region = api.getRegion(l);

        if (region == null) {
            return true;
        } else if (!(p instanceof Player)) {
            return false;
        }

        Player player = (Player) p;
        return switch (action) {
            case INTERACT_BLOCK -> region.canChest(player);
            case ATTACK_ENTITY, INTERACT_ENTITY -> region.canInteractPassives(player);
            case ATTACK_PLAYER -> region.canPVP(player, player);
            default -> region.canBuild(player);
        };
    }
}
