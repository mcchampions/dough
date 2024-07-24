package io.github.bakedlibs.dough.protection.modules;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import io.github.bakedlibs.dough.protection.ActionType;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;

public class WorldGuardProtectionModule implements ProtectionModule {
    private WorldGuardPlugin worldguard;
    private RegionContainer container;

    private final Plugin plugin;

    public WorldGuardProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        worldguard = WorldGuardPlugin.inst();
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
        container = platform.getRegionContainer();
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(l);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(l.getWorld());
        LocalPlayer player = worldguard.wrapOfflinePlayer(p);

        /*
         * if (platform.getSessionManager().hasBypass(player, world)) {
         * return true;
         * }
         */

        if (action.getType() != ActionType.BLOCK) {
            Set<ProtectedRegion> regions = container.get(world).getApplicableRegions(BlockVector3.at(l.getX(), l.getY(), l.getZ())).getRegions();

            if (regions.isEmpty()) {
                return true;
            } else {
                return container.createQuery().testState(loc, player, convert(action));
            }
        } else {
            return container.createQuery().testBuild(loc, player, convert(action));
        }
    }

    private static StateFlag convert(Interaction action) {
        return switch (action) {
            case ATTACK_PLAYER -> Flags.PVP;
            case ATTACK_ENTITY -> Flags.DAMAGE_ANIMALS;
            case INTERACT_BLOCK, INTERACT_ENTITY -> Flags.USE;
            case BREAK_BLOCK -> Flags.BLOCK_BREAK;
            case PLACE_BLOCK -> Flags.BLOCK_PLACE;
            default -> Flags.BUILD;
        };
    }
}
