package io.github.bakedlibs.dough.skins.nms;

import com.mojang.authlib.GameProfile;
import io.github.bakedlibs.dough.common.DoughLogger;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import java.util.logging.Level;
import io.papermc.lib.PaperLib;
import org.bukkit.block.Block;

public interface PlayerHeadAdapter {
    void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate);

    static PlayerHeadAdapter get() {
        try {
            MinecraftVersion version = MinecraftVersion.get();
            if (PaperLib.isPaper()) {
                try {
                    return new PlayerHeadAdapterPaper();
                } catch (Exception ignore) {}
            }
            if (version.isAtLeast(1, 21, 6)) {
                // 1.21.6 mappings
                return new PlayerHeadAdapter21v6();
            } else if (version.isAtLeast(1, 20, 5)) {
                // 1.20.5 mappings
                return new PlayerHeadAdapter20v5();
            } else if (version.isAtLeast(1, 18)) {
                // 1.18 mappings
                return new PlayerHeadAdapter18();
            } else {
                throw new RuntimeException("不支持的MC版本");
            }
        } catch (Exception x) {
            DoughLogger logger = new DoughLogger("skins");
            logger.log(Level.SEVERE, "Failed to detect skull nbt methods", x);
            return null;
        }

    }
}
