package io.github.bakedlibs.dough.protection.modules;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;
import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.Operation;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * Protection handling module for HuskTowns
 *
 * @author William278
 */
public class HuskTownsProtectionModule implements ProtectionModule {
    private BukkitHuskTownsAPI huskTownsAPI;
    private final Plugin plugin;

    public HuskTownsProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        huskTownsAPI = BukkitHuskTownsAPI.getInstance();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        // Offline player have no permissions
        if (!p.isOnline()) {
            return false;
        }

        // Convert the dough interaction to HuskTowns' ActionType and check via the API
        return huskTownsAPI.isOperationAllowed(Operation.of(
                huskTownsAPI.getOnlineUser(p.getPlayer()),
                getHuskTownsAction(action),
                huskTownsAPI.getPosition(l)
        ));
    }

    /**
     * Returns the corresponding HuskTowns {@link OperationType} from the dough {@link Interaction}
     *
     * @param doughAction The dough {@link Interaction}
     * @return The corresponding HuskTowns {@link OperationType}
     */
    public static OperationType getHuskTownsAction(Interaction doughAction) {
        return switch (doughAction) {
            case BREAK_BLOCK -> OperationType.BLOCK_BREAK;
            case PLACE_BLOCK -> OperationType.BLOCK_PLACE;
            case ATTACK_ENTITY -> OperationType.PLAYER_DAMAGE_ENTITY;
            case ATTACK_PLAYER -> OperationType.PLAYER_DAMAGE_PLAYER;
            case INTERACT_BLOCK -> OperationType.BLOCK_INTERACT;
            default -> OperationType.ENTITY_INTERACT;
        };
    }
}
