package nuclearkat.customitemeffects.items;

import nuclearkat.customitemeffects.CustomItemEffects;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ItemBuilder {

    private final CustomItemEffects customItemEffects = CustomItemEffects.getPlugin(CustomItemEffects.class);
    private final String displayName;
    private final Material materialType;
    private final List<String> lore;
    private final ItemStack createdItem;
    protected final int cooldown;
    private final Map<UUID, Long> cooldownMap = new HashMap<>();

    /**
     *
     * @param displayName The display name of the item.
     * @param materialType The material type of the item.
     * @param cooldown The cooldown in seconds of which the item's abilities will be delayed.
     * @param lore The lore of the item.
     */
    public ItemBuilder(String displayName, Material materialType, int cooldown, String... lore){
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.materialType = materialType;
        this.lore = Arrays.asList(lore);
        this.cooldown = cooldown * 1000;
        this.createdItem = createItemStack();
    }

    public abstract void onUse(Player player, Player target);

    /**
     *
     * @param player The player of which to check for a cooldown.
     * @return Returns true if the item is on cooldown for the player, otherwise false.
     */
    public boolean isOnCooldown(Player player){
        UUID playerId = player.getUniqueId();
        if(!cooldownMap.containsKey(playerId)){
            return false;
        }
        long lastUsed = cooldownMap.get(playerId);
        return System.currentTimeMillis() - lastUsed < cooldown;
    }

    public void applyCooldown(Player player){
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * A part of the item stack creation process, adds pdc for the specified parameters.
     *
     * @param itemStack The item stack to add pdc to.
     */
    protected void addCustomData(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            System.out.println("Item meta is null when creating item!");
            return;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(customItemEffects.getRegisteredKey("CustomItem"), PersistentDataType.STRING, displayName);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Creates the item stack with the specified parameters.
     *
     * @return Returns the created ItemStack.
     */
    protected ItemStack createItemStack(){
        ItemStack itemStack = new ItemStack(materialType);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);

        List<String> itemLore = lore.stream()
                .map(l -> ChatColor.translateAlternateColorCodes('&', l))
                .collect(Collectors.toList());

        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        addCustomData(itemStack);

        return itemStack;
    }

    public ItemStack getItemStack(){
        return createdItem;
    }
}
