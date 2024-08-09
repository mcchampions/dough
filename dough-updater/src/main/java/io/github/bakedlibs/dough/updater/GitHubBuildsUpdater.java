package io.github.bakedlibs.dough.updater;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.bakedlibs.dough.common.CommonPatterns;
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
