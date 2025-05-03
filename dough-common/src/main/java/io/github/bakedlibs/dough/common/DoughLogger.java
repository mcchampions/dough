package io.github.bakedlibs.dough.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginLogger;

public class DoughLogger extends Logger {
    public DoughLogger(Server server, String name) {
        super("dough: " + name, null);

        setParent(server.getLogger());
        setLevel(Level.ALL);
    }

    public DoughLogger(String name) {
        this(Bukkit.getServer(), name);
    }

}
