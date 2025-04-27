package io.github.bakedlibs.dough.protection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import io.github.bakedlibs.dough.common.DoughLogger;
import io.github.bakedlibs.dough.protection.loggers.CoreProtectLogger;
import io.github.bakedlibs.dough.protection.loggers.LogBlockLogger;
import io.github.bakedlibs.dough.protection.modules.BentoBoxProtectionModule;
import io.github.bakedlibs.dough.protection.modules.BlockLockerProtectionModule;
import io.github.bakedlibs.dough.protection.modules.BoltProtectionModule;
import io.github.bakedlibs.dough.protection.modules.ChestProtectProtectionModule;
import io.github.bakedlibs.dough.protection.modules.FactionsUUIDProtectionModule;
import io.github.bakedlibs.dough.protection.modules.FunnyGuildsProtectionModule;
import io.github.bakedlibs.dough.protection.modules.GriefPreventionProtectionModule;
import io.github.bakedlibs.dough.protection.modules.HuskTownsProtectionModule;
import io.github.bakedlibs.dough.protection.modules.HuskClaimsProtectionModule;
import io.github.bakedlibs.dough.protection.modules.LWCProtectionModule;
import io.github.bakedlibs.dough.protection.modules.LandsProtectionModule;
import io.github.bakedlibs.dough.protection.modules.LocketteProtectionModule;
import io.github.bakedlibs.dough.protection.modules.PlotSquaredProtectionModule;
import io.github.bakedlibs.dough.protection.modules.PreciousStonesProtectionModule;
import io.github.bakedlibs.dough.protection.modules.RedProtectProtectionModule;
import io.github.bakedlibs.dough.protection.modules.ShopChestProtectionModule;
import io.github.bakedlibs.dough.protection.modules.TownyProtectionModule;
import io.github.bakedlibs.dough.protection.modules.WorldGuardProtectionModule;
import io.github.bakedlibs.dough.protection.modules.DominionProtectionModule;


/**
 * This Class provides a nifty API for plugins to query popular protection plugins.
 *
 * @author TheBusyBiscuit
 *
 */
public final class ProtectionManager {
    private final Set<ProtectionModule> protectionModules = new HashSet<>();
    private final Set<ProtectionLogger> protectionLoggers = new HashSet<>();
    private final Logger logger;

    /**
     * This creates a new instance of {@link ProtectionManager}, you can see this
     * as a "Snapshot" of your plugins too.
     *
     * @param plugin
     *            The plugin instance that integrates dough.
     */
    public ProtectionManager(Plugin plugin) {
        logger = new DoughLogger(plugin.getServer(), "protection");

        logger.log(Level.INFO, "Loading Protection Modules...");
        logger.log(Level.INFO, "This may happen more than once.");

        /*
         * The following plugins are protection plugins.
         */
        loadModuleImplementations(plugin);

        /*
         * The following Plugins are logging plugins, not protection plugins.
         */
        loadLoggerImplementations(plugin);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadModuleImplementations(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        // We sadly cannot use ModuleName::new as this would load the class into memory prematurely
        registerModule(pm, "WorldGuard", var -> new WorldGuardProtectionModule(var));
        registerModule(pm, "Towny", var -> new TownyProtectionModule(var));
        registerModule(pm, "GriefPrevention", var -> new GriefPreventionProtectionModule(var));
        registerModule(pm, "LWC", var -> new LWCProtectionModule(var));
        registerModule(pm, "PreciousStones", var -> new PreciousStonesProtectionModule(var));
        registerModule(pm, "Lockette", var -> new LocketteProtectionModule(var));
        registerModule(pm, "RedProtect", var -> new RedProtectProtectionModule(var));
        registerModule(pm, "BentoBox", var -> new BentoBoxProtectionModule(var));
        registerModule(pm, "BlockLocker", var -> new BlockLockerProtectionModule(var));
        registerModule(pm, "Lands", lands -> new LandsProtectionModule(lands, plugin));
        registerModule(pm, "ChestProtect", var -> new ChestProtectProtectionModule(var));
        registerModule(pm, "Factions", var -> new FactionsUUIDProtectionModule(var));
        registerModule(pm, "FunnyGuilds", var -> new FunnyGuildsProtectionModule(var));
        registerModule(pm, "PlotSquared", var -> new PlotSquaredProtectionModule(var));
        registerModule(pm, "HuskTowns", var -> new HuskTownsProtectionModule(var));
        registerModule(pm, "ShopChest", var -> new ShopChestProtectionModule(var));
        registerModule(pm, "HuskClaims", var -> new HuskClaimsProtectionModule(var));
        registerModule(pm, "Bolt", var -> new BoltProtectionModule(var));
        registerModule(pm, "Dominion", var -> new DominionProtectionModule(var));
        /*
         * The following Plugins work by utilising one of the above listed
         * Plugins in the background.
         * We do not need a module for them, but let us make the server owner
         * aware that this compatibility exists.
         */
        if (pm.isPluginEnabled("ProtectionStones")) {
            printModuleLoaded("ProtectionStones");
        }

        if (pm.isPluginEnabled("uSkyblock")) {
            printModuleLoaded("uSkyblock");
        }
    }

    private void loadLoggerImplementations(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        if (pm.isPluginEnabled("CoreProtect")) {
            registerLogger(new CoreProtectLogger());
        }
        if (pm.isPluginEnabled("LogBlock")) {
            registerLogger(new LogBlockLogger());
        }
    }

    public void registerLogger(String name, ProtectionLogger module) {
        protectionLoggers.add(module);
        printModuleLoaded(name);
    }

    public void registerModule(PluginManager pm, String pluginName, Function<Plugin, ProtectionModule> constructor) {
        Plugin plugin = pm.getPlugin(pluginName);

        if (plugin != null && plugin.isEnabled()) {
            registerModule(plugin, constructor);
        }
    }

    private void registerModule(Plugin plugin, Function<Plugin, ProtectionModule> constructor) {
        try {
            ProtectionModule module = constructor.apply(plugin);
            module.load();

            protectionModules.add(module);
            printModuleLoaded(module.getName() + " v" + module.getVersion());
        } catch (Throwable x) {
            logger.log(Level.SEVERE, x, () -> "An Error occured while registering the Protection Module: \"" + plugin.getName() + "\" v" + plugin.getDescription().getVersion());
        }
    }

    public void registerLogger(ProtectionLogger module) {
        try {
            module.load();
            registerLogger(module.getName(), module);
        } catch (Throwable x) {
            logger.log(Level.SEVERE, x, () -> "An Error occured while registering the Protection Module: \"" + module.getName() + "\"");
        }
    }

    private void printModuleLoaded(String module) {
        logger.log(Level.INFO, "Loaded Protection Module \"{0}\"", module);
    }

    public boolean hasPermission(OfflinePlayer p, Block b, Interaction action) {
        return hasPermission(p, b.getLocation(), action);
    }

    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        for (ProtectionModule module : protectionModules) {
            try {
                if (!module.hasPermission(p, l, action)) {
                    return false;
                }
            } catch (Exception | LinkageError x) {
                logger.log(Level.SEVERE, x, () -> "An Error occured while querying the Protection Module: \"" + module.getName() + " v" + module.getVersion() + "\"");
                // Fallback will just be "allow".
                return true;
            }
        }

        return true;
    }

    public void logAction(OfflinePlayer p, Block b, Interaction action) {
        for (ProtectionLogger module : protectionLoggers) {
            try {
                module.logAction(p, b, action);
            } catch (Exception | LinkageError x) {
                logger.log(Level.SEVERE, x, () -> "An Error occured while logging for the Protection Module: \"" + module.getName() + "\"");
            }
        }
    }

}
