package io.github.bakedlibs.dough.updater;

import io.github.bakedlibs.dough.versions.PrefixedVersion;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BlobBuildUpdater extends AbstractPluginUpdater<PrefixedVersion> {
    public BlobBuildUpdater(Plugin plugin, File file, String project) {
        this(plugin, file, project, "Dev");
    }

    public BlobBuildUpdater(Plugin plugin, File file, String project, String releaseChannel) {
        super(plugin, file, extractBuild(releaseChannel + " - ", plugin));
    }

    @Override
    public void start() {}
}
