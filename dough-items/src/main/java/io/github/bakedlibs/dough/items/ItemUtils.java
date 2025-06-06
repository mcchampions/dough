package io.github.bakedlibs.dough.items;

import io.github.bakedlibs.dough.items.nms.ItemNameAdapter;

import java.util.List;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * A utility class providing some methods to handle {@link ItemStack}s.
 * 
 * @author TheBusyBiscuit
 *
 */
public final class ItemUtils {
    private static final ItemNameAdapter adapter = ItemNameAdapter.get();

    private ItemUtils() {}

    /**
     * This method returns a human-readable version of this item's name.
     * If the specified {@link ItemStack} has a Custom Display Name, it will return that.
     * Otherwise it will return the english name of it's {@link Material}
     * 
     * @param item
     *            The Item to format
     * 
     * @return The formatted Item Name
     */
    public static String getItemName(ItemStack item) {
        if (item == null) {
            return "null";
        } else if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }

        if (adapter != null) {
            try {
                return adapter.getName(item);
            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR";
            }
        } else {
            return "unknown";
        }
    }

    /**
     * This method compares two instances of {@link ItemStack} and checks
     * whether their {@link Material} and {@link ItemMeta} match.
     * 
     * @param a
     *            {@link ItemStack} One
     * @param b
     *            {@link ItemStack} Two
     * @return Whether the two instances of {@link ItemStack} are similiar and can be stacked.
     */
    public static boolean canStack(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.getType() != b.getType() || a.hasItemMeta() != b.hasItemMeta()) {
            return false;
        }

        if (a.hasItemMeta()) {
            ItemMeta aMeta = a.getItemMeta();
            ItemMeta bMeta = b.getItemMeta();

            // Item Damage
            if (aMeta instanceof Damageable != bMeta instanceof Damageable) {
                return false;
            }

            if (aMeta instanceof Damageable && ((Damageable) aMeta).getDamage() != ((Damageable) bMeta).getDamage()) {
                return false;
            }

            // Leather Armor Color
            if (aMeta instanceof LeatherArmorMeta != bMeta instanceof LeatherArmorMeta) {
                return false;
            }

            if (aMeta instanceof LeatherArmorMeta && !((LeatherArmorMeta) aMeta).getColor().equals(((LeatherArmorMeta) bMeta).getColor())) {
                return false;
            }

            // Custom Model Data
            if (aMeta.hasCustomModelData() != bMeta.hasCustomModelData()) {
                return false;
            }

            if (aMeta.hasCustomModelData() && aMeta.getCustomModelData() != bMeta.getCustomModelData()) {
                return false;
            }

            // Enchantments
            if (!aMeta.getEnchants().equals(bMeta.getEnchants())) {
                return false;
            }

            // Display Name
            if (aMeta.hasDisplayName() != bMeta.hasDisplayName()) {
                return false;
            }

            if (aMeta.hasDisplayName() && !aMeta.displayName().equals(bMeta.displayName())) {
                return false;
            }

            // PersistentDataContainer
            if (!aMeta.getPersistentDataContainer().equals(bMeta.getPersistentDataContainer())) {
                return false;
            }

            // Lore
            if (aMeta.hasLore() != bMeta.hasLore()) {
                return false;
            }

            if (aMeta.hasLore()) {
                List<Component> aLore = aMeta.lore();
                List<Component> bLore = bMeta.lore();

                if (aLore.size() != bLore.size()) {
                    return false;
                }

                for (int i = 0; i < aLore.size(); i++) {
                    if (!aLore.get(i).equals(bLore.get(i))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * This method damages the specified Item by 1.
     * If ignoredEnchantments is set to false, it will factor in the "Unbreaking" Enchantment.
     * 
     * @param item
     *            The Item to damage
     * @param ignoreEnchantments
     *            Whether the Unbreaking Enchantment should be ignored
     */
    public static void damageItem(ItemStack item, boolean ignoreEnchantments) {
        damageItem(item, 1, ignoreEnchantments);
    }

    /**
     * This method damages the specified Item by the given amount.
     * If ignoredEnchantments is set to false, it will factor in the "Unbreaking" Enchantment.
     * 
     * @param item
     *            The Item to damage
     * @param damage
     *            The amount of damage to apply
     * @param ignoreEnchantments
     *            Whether the Unbreaking Enchantment should be ignored
     */
    public static void damageItem(ItemStack item, int damage, boolean ignoreEnchantments) {
        if (item.getType() != Material.AIR && item.getAmount() > 0) {
            if (item.getItemMeta() != null && item.getItemMeta().isUnbreakable()) {
                return;
            }

            int remove = damage;

            if (!ignoreEnchantments && item.getEnchantments().containsKey(Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking")))) {
                int level = item.getEnchantmentLevel(Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking")));

                for (int i = 0; i < damage; i++) {
                    if (Math.random() * 100 <= (60 + Math.floorDiv(40, (level + 1)))) {
                        remove--;
                    }
                }
            }

            ItemMeta meta = item.getItemMeta();
            Damageable damageable = (Damageable) meta;

            if (damageable.getDamage() + remove > item.getType().getMaxDurability()) {
                item.setAmount(0);
            } else {
                damageable.setDamage(damageable.getDamage() + remove);
                item.setItemMeta(meta);
            }
        }
    }

    /**
     * This Method will consume the Item in the specified slot.
     * See {@link ItemUtils#consumeItem(ItemStack, int, boolean)} for further details.
     * 
     * @param item
     *            The Item to consume
     * @param replaceConsumables
     *            Whether Consumable Items should be replaced with their "empty" version, see
     *            {@link ItemUtils#consumeItem(ItemStack, int, boolean)}
     */
    public static void consumeItem(ItemStack item, boolean replaceConsumables) {
        consumeItem(item, 1, replaceConsumables);
    }

    /**
     * This Method consumes a specified amount of items from the
     * specified slot.
     * <p>
     * The items will be removed from the slot, if the slot does not hold enough items,
     * it will be replaced with null.
     * Note that this does not check whether there are enough Items present,
     * if you specify a bigger amount than present, it will simply set the Item to null.
     * <p>
     * If replaceConsumables is true, the following things will not be replaced with 'null':
     * {@code Buckets -> new ItemStack(Material.BUCKET)}
     * {@code Potions -> new ItemStack(Material.GLASS_BOTTLE)}
     * 
     * @param item
     *            The Item to consume
     * @param amount
     *            How many Items should be removed
     * @param replaceConsumables
     *            Whether Items should be replaced with their "empty" version
     */
    public static void consumeItem(ItemStack item, int amount, boolean replaceConsumables) {
        if (item.getType() != Material.AIR && item.getAmount() > 0) {
            if (replaceConsumables) {
                switch (item.getType()) {
                    case POTION:
                    case DRAGON_BREATH:
                    case HONEY_BOTTLE:
                        item.setType(Material.GLASS_BOTTLE);
                        item.setAmount(1);
                        return;
                    case WATER_BUCKET:
                    case LAVA_BUCKET:
                    case MILK_BUCKET:
                        item.setType(Material.BUCKET);
                        item.setAmount(1);
                        return;
                    default:
                        break;
                }
            }

            if (item.getAmount() <= amount) {
                item.setAmount(0);
            } else {
                item.setAmount(item.getAmount() - amount);
            }
        }
    }

}
