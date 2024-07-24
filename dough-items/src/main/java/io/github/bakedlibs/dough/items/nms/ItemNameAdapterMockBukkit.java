package io.github.bakedlibs.dough.items.nms;

import java.util.Locale;

import org.bukkit.inventory.ItemStack;

class ItemNameAdapterMockBukkit implements ItemNameAdapter {

    @Override
    public String getName(ItemStack item) {
        return item.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

}
