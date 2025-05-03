package io.github.bakedlibs.dough.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.ItemUtils;

public final class InvUtils {
    private InvUtils() {}

    public static boolean hasEmptySlot(Inventory inv) {
        return inv.firstEmpty() != 1;
    }

    public static boolean isEmpty(Inventory inv) {
        // Sadly Inventory#isEmpty() is not available everywhere

        for (ItemStack item : inv) {
            if (item != null && !item.getType().isAir()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidStackSize(ItemStack stack, ItemStack item, Inventory inv) {
        int newStackSize = stack.getAmount() + item.getAmount();
        return newStackSize <= stack.getMaxStackSize() && newStackSize <= inv.getMaxStackSize();
    }

    public static boolean isItemAllowed(Material itemType, InventoryType inventoryType) {
        return switch (inventoryType) {
            case LECTERN ->
                // Lecterns only allow written books or writable books
                    itemType == Material.WRITABLE_BOOK || itemType == Material.WRITTEN_BOOK;
            case SHULKER_BOX ->
                // Shulker Boxes do not allow Shulker boxes
                    itemType != Material.SHULKER_BOX && !itemType.name().endsWith("_SHULKER_BOX");
            default -> true;
        };
    }

    public static boolean fits(Inventory inv, ItemStack item, int... slots) {
        if (!isItemAllowed(item.getType(), inv.getType())) {
            return false;
        }

        if (slots.length == 0) {
            slots = IntStream.range(0, inv.getSize()).toArray();
        }

        for (int slot : slots) {
            ItemStack stack = inv.getItem(slot);

            if (stack == null || stack.getType() == Material.AIR) {
                return true;
            }

            if (isValidStackSize(stack, item, inv) && ItemUtils.canStack(stack, item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean fitAll(Inventory inv, ItemStack[] items, int... slots) {
        if (slots.length == 0) {
            slots = IntStream.range(0, inv.getSize()).toArray();
        }

        if (items.length == 0) {
            return true;
        } else if (items.length == 1) {
            return fits(inv, items[0], slots);
        }

        Map<Integer, ItemStack> cache = new HashMap<>();

        for (ItemStack item : items) {
            boolean resolved = false;

            for (int slot : slots) {
                ItemStack stack = cache.getOrDefault(slot, inv.getItem(slot));

                if (stack == null || stack.getType() == Material.AIR) {
                    cache.put(slot, item);
                    resolved = true;
                } else if (isValidStackSize(stack, item, inv) && ItemUtils.canStack(stack, item)) {
                    ItemStack clone = stack.clone();
                    clone.setAmount(stack.getAmount() + item.getAmount());
                    cache.put(slot, clone);
                    resolved = true;
                }

                if (resolved) {
                    break;
                }
            }

            if (!resolved) {
                return false;
            }
        }

        return true;
    }

    public static boolean removeItem(Inventory inv, int amount, boolean replaceConsumables, Predicate<ItemStack> predicate) {
        int removed = 0;
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack item = inv.getItem(slot);

            if (item != null && predicate.test(item)) {
                if (item.getAmount() + removed >= amount) {
                    ItemUtils.consumeItem(item, amount - removed, replaceConsumables);
                    return true;
                } else if (item.getAmount() > 0) {
                    removed += item.getAmount();
                    ItemUtils.consumeItem(item, item.getAmount(), replaceConsumables);
                }
            }
        }

        return false;
    }
}