package io.github.bakedlibs.dough.protection.modules;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class DominionProtectionModule implements ProtectionModule {

    private final Plugin plugin;
    private DominionAPI api;

    public DominionProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Use this method to load instances of your API or other utilites you need
     */
    @Override
    public void load() {
        try {
            api = DominionAPI.getInstance();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load Dominion API. Disabling Dominion Protection Module.");
        }
    }

    /**
     * This returns the {@link Plugin} for this {@link ProtectionModule}.
     *
     * @return The associated {@link Plugin}
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * This method implements the functionality of this module.
     * Use it to allow or deny an Action based on the rules of your Protection {@link Plugin}
     *
     * @param p      The Player that is being queried, can be offline
     * @param l      The {@link Location} of the event that is happening
     * @param action The {@link Interaction} that is taking place.
     * @return Whether the action was allowed by your {@link Plugin}
     */
    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (!p.isOnline()) {
            return false;
        }
        Player player = p.getPlayer();
        if (player == null) {
            return true;
        }
        Flag flag = getDominionFlagFromInteraction(action);
        if (flag == null) {
            return true;
        }
        DominionDTO dominion = api.getDominion(l);
        if (dominion == null) {
            return true;
        }
        if (flag instanceof PriFlag preFlag) {
            return api.checkPrivilegeFlag(dominion, preFlag, player);
        } else {
            EnvFlag envFlag = (EnvFlag) flag;
            return api.checkEnvironmentFlag(dominion, envFlag);
        }
    }

    private static @Nullable Flag getDominionFlagFromInteraction(@Nonnull Interaction action) {
        return switch (action) {
            case BREAK_BLOCK -> Flags.BREAK_BLOCK;
            case PLACE_BLOCK -> Flags.PLACE;
            case INTERACT_BLOCK -> Flags.CONTAINER;
            case ATTACK_PLAYER -> Flags.PVP;
            default -> null;
        };
    }
}
