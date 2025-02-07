package io.github.bakedlibs.dough.items.nms;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemNameAdapterPaper implements ItemNameAdapter {
    @Override
    public String getName(ItemStack item){
        return PlainTextComponentSerializer.plainText().serialize(Bukkit.getItemFactory().displayName(item));
    }
}
