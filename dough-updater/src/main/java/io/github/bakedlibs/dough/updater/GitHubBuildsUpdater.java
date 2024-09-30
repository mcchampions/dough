package io.github.bakedlibs.dough.updater;

import java.io.File;

import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.versions.PrefixedVersion;

public class GitHubBuildsUpdater extends AbstractPluginUpdater<PrefixedVersion> {
    public GitHubBuildsUpdater(Plugin plugin, File file, String repo) {
        this(plugin, file, repo, "DEV - ");
    }

    public GitHubBuildsUpdater(Plugin plugin, File file, String repo, String prefix) {
        super(plugin, file, extractBuild(prefix, plugin));

    }

    @Override
    public void start() {}
}
