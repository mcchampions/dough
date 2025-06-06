package io.github.bakedlibs.dough.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemStackSnapshot extends ItemStack {
    private static final String ERROR_MESSAGE = "ItemStackSnapshots are immutable and not intended for actual usage.";

    private final ItemMeta meta;
    private final int amount;
    private final boolean hasItemMeta;

    private ItemStackSnapshot(ItemStack item) {
        super(item.getType());

        amount = item.getAmount();
        hasItemMeta = item.hasItemMeta();

        if (hasItemMeta) {
            meta = item.getItemMeta();
        } else {
            meta = null;
        }
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta;
    }

    @Override
    public ItemMeta getItemMeta() {
        return meta;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ItemStack clone() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setType(Material type) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setAmount(int amount) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * Creates an {@link ItemStackSnapshot} of an {@link ItemStack}. This method
     * will not check if the passed {@link ItemStack} has already been wrapped
     *
     * @param itemStack
     *            The {@link ItemStack} to wrap
     * 
     * @return Returns an {@link ItemStackSnapshot} of the passed {@link ItemStack}
     * @see #wrap(ItemStack)
     */
    public static ItemStackSnapshot forceWrap(ItemStack itemStack) {
        return new ItemStackSnapshot(itemStack);
    }

    /**
     * Creates an {@link ItemStackSnapshot} of an {@link ItemStack}. This method
     * will return the the casted reference of the passed {@link ItemStack} if it
     * is already an {@link ItemStackSnapshot}
     *
     * @param itemStack
     *            The {@link ItemStack} to wrap
     * 
     * @return Returns an {@link ItemStackSnapshot} of the passed {@link ItemStack}
     * @see #forceWrap(ItemStack)
     */
    public static ItemStackSnapshot wrap(ItemStack itemStack) {
        if (itemStack instanceof ItemStackSnapshot) {
            return (ItemStackSnapshot) itemStack;
        }
        return new ItemStackSnapshot(itemStack);
    }

    /**
     * This creates an {@link ItemStackSnapshot} array from a given {@link ItemStack} array.
     * 
     * @param items
     *            The array of {@link ItemStack ItemStacks} to transform
     * 
     * @return An {@link ItemStackSnapshot} array
     */
    public static ItemStackSnapshot[] wrapArray(ItemStack[] items) {
        ItemStackSnapshot[] array = new ItemStackSnapshot[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                array[i] = wrap(items[i]);
            }
        }
        return array;
    }

    /**
     * This creates an {@link ItemStackSnapshot} {@link List} from a given {@link ItemStack} {@link List} *
     * 
     * @param items
     *            The {@link List} of {@link ItemStack ItemStacks} to transform
     * 
     * @return An {@link ItemStackSnapshot} array
     */
    public static List<ItemStackSnapshot> wrapList(List<ItemStack> items) {
                List<ItemStackSnapshot> list = new ArrayList<>(items.size());
        for (ItemStack item : items) {
            if (item != null) {
                list.add(wrap(item));
            } else {
                list.add(null);
            }
        }
        return list;
    }
}
