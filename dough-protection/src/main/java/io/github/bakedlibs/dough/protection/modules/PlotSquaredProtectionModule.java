package io.github.bakedlibs.dough.protection.modules;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.permissions.Permission;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;

public class PlotSquaredProtectionModule implements ProtectionModule {
    private final Plugin plugin;

    public PlotSquaredProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        // We don't need to load any APIs, everything is static
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, org.bukkit.Location l, Interaction action) {
        Block b = l.getBlock();
        Location location = Location.at(b.getWorld().getName(), b.getX(), b.getY(), b.getZ());

        if (location.isPlotRoad()) {
            return check(p, action);
        }

        Plot plot = location.getOwnedPlot();
        return plot == null || plot.isAdded(p.getUniqueId()) || check(p, action);
    }

    private boolean check(OfflinePlayer p, Interaction action) {
        PlotPlayer<OfflinePlayer> player = PlotPlayer.from(p);

        return switch (action) {
            case INTERACT_BLOCK -> player.hasPermission(Permission.PERMISSION_ADMIN_INTERACT_UNOWNED);
            case ATTACK_PLAYER -> player.hasPermission(Permission.PERMISSION_ADMIN_PVP);
            default -> player.hasPermission(Permission.PERMISSION_ADMIN_BUILD_UNOWNED);
        };
    }
}
