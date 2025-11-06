package io.github.bakedlibs.dough.updater;

import java.net.URL;

import io.github.bakedlibs.dough.versions.Version;

abstract class UpdaterTask<V extends Version> implements Runnable {
    UpdaterTask(PluginUpdater<V> updater, URL url) {}

    public abstract UpdateInfo parse(String result);

    @Override
    public void run() {}
}