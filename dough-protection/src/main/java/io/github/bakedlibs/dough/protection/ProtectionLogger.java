package io.github.bakedlibs.dough.protection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public interface ProtectionLogger {

    void load();

    String getName();

    void logAction(OfflinePlayer p, Block b, Interaction action);

}
