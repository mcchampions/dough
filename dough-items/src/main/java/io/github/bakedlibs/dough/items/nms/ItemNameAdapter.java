package io.github.bakedlibs.dough.items.nms;

import java.util.logging.Level;

import io.papermc.lib.PaperLib;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.DoughLogger;
import io.github.bakedlibs.dough.versions.MinecraftVersion;

public interface ItemNameAdapter {
    String getName(ItemStack item);

    static ItemNameAdapter get() {
        try {
            MinecraftVersion version = MinecraftVersion.get();

            if (version.isAtLeast(1, 19, 4) && PaperLib.isPaper()) {

                return new ItemNameAdapterPaper();
            }

            if (version.isAtLeast(1, 20, 5)) {
                return new ItemNameAdapter20v5();
            } else if (version.isAtLeast(1, 20)) {
                return new ItemNameAdapter20();
            } else if (version.isAtLeast(1, 19)) {
                return new ItemNameAdapter19();
            } else if (version.isAtLeast(1, 18, 2)) {
                return new ItemNameAdapter18v2();
            } else if (version.isAtLeast(1, 18)) {
                // 1.18+ mappings
                return new ItemNameAdapter18();
            } else {
                throw new RuntimeException("不支持的MC版本");
            }
        } catch (Exception x) {
            DoughLogger logger = new DoughLogger("items");
            logger.log(Level.SEVERE, "Failed to detect items nbt methods", x);
            return null;
        }

    }
}
