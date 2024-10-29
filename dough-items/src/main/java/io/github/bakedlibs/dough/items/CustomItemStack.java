package io.github.bakedlibs.dough.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

public final class CustomItemStack {
    private CustomItemStack() {
    }

    public static ItemStack create(ItemStack itemStack, Consumer<ItemMeta> metaConsumer) {
        return new ItemStackEditor(itemStack).andMetaConsumer(metaConsumer).create();
    }

    public static ItemStack create(Material material, Consumer<ItemMeta> metaConsumer) {
        return new ItemStackEditor(material).andMetaConsumer(metaConsumer).create();
    }

    public static ItemStack create(ItemStack item, String name, String... lore) {
        return new ItemStackEditor(item)
                .setDisplayName(name)
                .setLore(lore)
                .create();
    }

    public static ItemStack create(Material material, String name, String... lore) {
        return create(new ItemStack(material), name, lore);
    }

    public static ItemStack create(Material type, String name, List<String> lore) {
        return create(new ItemStack(type), name, lore.toArray(String[]::new));
    }


    public static ItemStack create(ItemStack item, List<String> list) {
        return create(new ItemStack(item), list.get(0), list.subList(1, list.size()).toArray(String[]::new));
    }

    public static ItemStack create(Material type, List<String> list) {
        return create(new ItemStack(type), list);
    }

    public static ItemStack create(ItemStack item, int amount) {
        return new ItemStackEditor(item).setAmount(amount).create();
    }

    /**
     * Clones the item stack and sets its type
     *
     * @param itemStack The item
     * @param type      The new type
     * @return Returns the item with a new type
     * @deprecated Setting the type via {@link ItemStack#setType(Material)} will not be supported soon.
     */
    public static ItemStack create(ItemStack itemStack, Material type) {
        return new ItemStackEditor(itemStack).andStackConsumer(item -> item.setType(type)).create();
    }
}