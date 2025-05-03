package io.github.bakedlibs.dough.items;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * This is an immutable version of ItemMeta.
 * Use this class to optimize your ItemStack#getItemMeta() calls by returning
 * a field of this immutable copy.
 * <p>
 * This does not support {@link PersistentDataContainer} at the moment.
 *
 * @author TheBusyBiscuit
 */
public class ItemMetaSnapshot {
    private static final Pattern PATTERN = Pattern.compile("§([A-Z])");
    @SuppressWarnings("OptionalAssignedToNull")
    private Optional<String> displayName;
    private final Optional<Component> displayNameC;
    private final Optional<List<String>> lore;
    private final OptionalInt customModelData;

    private final Set<ItemFlag> itemFlags;
    private final Map<Enchantment, Integer> enchantments;

    public ItemMetaSnapshot(ItemStack item) {
        this(item.getItemMeta());
    }

    public ItemMetaSnapshot(Supplier<ItemMeta> supplier) {
        this(supplier.get());
    }

    public ItemMetaSnapshot(ItemMeta meta) {
        this.lore = meta.hasLore() ? Optional.of(Collections.unmodifiableList(meta.getLore())) : Optional.empty();
        this.customModelData = meta.hasCustomModelData() ? OptionalInt.of(meta.getCustomModelData()) : OptionalInt.empty();
        this.displayNameC = meta.hasDisplayName() ? Optional.of(meta.displayName()) : Optional.empty();

        this.itemFlags = meta.getItemFlags();
        this.enchantments = meta.getEnchants();
    }

    public Optional<String> getDisplayName() {
        //noinspection OptionalAssignedToNull
        if (displayName == null) {
            if (displayNameC.isEmpty()) {
                displayName = Optional.empty();
                return displayName;
            }
            this.displayName = Optional.of(PATTERN.matcher(LegacyComponentSerializer.legacySection().serialize(displayNameC.get())).replaceAll(m -> m.group().toLowerCase()));
        }
        return displayName;
    }

    public Optional<Component> displayName() {
        return displayNameC;
    }

    public Optional<List<String>> getLore() {
        return lore;
    }

    public OptionalInt getCustomModelData() {
        return customModelData;
    }

    public Set<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public boolean isSimilar(ItemMetaSnapshot snapshot) {
        if (snapshot.displayNameC.isPresent() != displayNameC.isPresent()) {
            return false;
        } else if (snapshot.displayNameC.isPresent() && !snapshot.displayNameC.get().equals(displayNameC.get())) {
            return false;
        } else if (snapshot.lore.isPresent() && lore.isPresent()) {
            return lore.get().equals(snapshot.lore.get());
        } else {
            return snapshot.lore.isEmpty() && lore.isEmpty();
        }
    }

    public boolean isSimilar(ItemMeta meta) {
        boolean hasDisplayName = meta.hasDisplayName();

        if (hasDisplayName != displayNameC.isPresent()) {
            return false;
        } else //noinspection DataFlowIssue
            if (hasDisplayName && !meta.displayName().equals(displayNameC.get())) {
                return false;
            } else {
                boolean hasLore = meta.hasLore();

                if (hasLore && lore.isPresent()) {
                    return lore.get().equals(meta.getLore());
                } else {
                    return !hasLore && lore.isEmpty();
                }
            }
    }

}
