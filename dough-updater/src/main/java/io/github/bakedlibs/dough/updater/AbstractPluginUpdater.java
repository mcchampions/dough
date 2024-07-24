package io.github.bakedlibs.dough.updater;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.versions.PrefixedVersion;
import io.github.bakedlibs.dough.versions.Version;

abstract class AbstractPluginUpdater<V extends Version> implements PluginUpdater<V> {

    private final Plugin plugin;
    private final File file;

    private int connectionTimeout = 9000;

    protected final V currentVersion;
    protected final CompletableFuture<V> latestVersion = new CompletableFuture<>();

    protected AbstractPluginUpdater(Plugin plugin, File file, V currentVersion) {
        Validate.notNull(plugin, "The plugin cannot be null.");
        Validate.notNull(file, "The plugin file cannot be null.");
        Validate.notNull(currentVersion, "The current version cannot be null.");

        this.plugin = plugin;
        this.file = file;
        this.currentVersion = currentVersion;

        prepareUpdateFolder();
    }

    private void prepareUpdateFolder() {
        File dir = new File("plugins/" + Bukkit.getUpdateFolder());

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int timeout) {
        Validate.isTrue(timeout > 0, "Timeout must be positive.");

        this.connectionTimeout = timeout;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public File getFile() {
        return file;
    }

    @Override
    public V getCurrentVersion() {
        return currentVersion;
    }

    public CompletableFuture<V> getLatestVersion() {
        return latestVersion;
    }

    protected void scheduleAsyncUpdateTask(UpdaterTask<V> task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    protected static PrefixedVersion extractBuild(String prefix, Plugin plugin) {
        String pluginVersion = plugin.getDescription().getVersion();

        if (pluginVersion.startsWith(prefix)) {
            int version = Integer.parseInt(pluginVersion.substring(prefix.length()).split(" ")[0], 10);
            return new PrefixedVersion(prefix, version);
        } else {
            throw new IllegalArgumentException("Not a valid build version: " + pluginVersion);
        }
    }
}
