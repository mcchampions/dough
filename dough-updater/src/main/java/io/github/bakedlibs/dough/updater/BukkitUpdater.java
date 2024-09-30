package io.github.bakedlibs.dough.updater;

import java.io.File;
import java.util.Locale;

import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.versions.SemanticVersion;

public class BukkitUpdater extends AbstractPluginUpdater<SemanticVersion> {
    public BukkitUpdater(Plugin plugin, File file, int id) {
        super(plugin, file, getVersion(plugin));
    }

    private static SemanticVersion getVersion(Plugin plugin) {
        String pluginVersion = plugin.getDescription().getVersion().toLowerCase(Locale.ROOT);
        return SemanticVersion.parse(pluginVersion);
    }

    @Override
    public void start() {}
}
