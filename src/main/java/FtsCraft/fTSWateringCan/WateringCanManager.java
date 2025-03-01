package FtsCraft.fTSWateringCan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class WateringCanManager implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey wateringCanKey;
    private final NamespacedKey wateringCanIdKey;
    private final NamespacedKey toolHandleKey;
    private final NamespacedKey toolMoldKey;

    public WateringCanManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.wateringCanKey = new NamespacedKey(plugin, "watering_can");
        this.wateringCanIdKey = new NamespacedKey(plugin, "watering_can_id");
        this.toolMoldKey = new NamespacedKey(plugin, "tool_mold");
        this.toolHandleKey = new NamespacedKey(plugin, "tool_handle");
    }

    public NamespacedKey getWateringCanIdKey() {
        return wateringCanIdKey;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);
        if (leftItem != null && leftItem.hasItemMeta() &&
                leftItem.getItemMeta().getPersistentDataContainer().has(wateringCanKey, PersistentDataType.BYTE)) {
            if (rightItem != null && rightItem.hasItemMeta() &&
                    rightItem.getItemMeta().getPersistentDataContainer().has(toolHandleKey, PersistentDataType.BYTE)) {
                ItemStack repaired = leftItem.clone();
                ItemMeta meta = repaired.getItemMeta();
                if (meta instanceof Damageable damageable) {
                    damageable.setDamage(0);
                }
                inventory.setRepairCost(0); // Marked for Removal but works, might need to change it in later updates.
                repaired.setItemMeta(meta);
                event.setResult(repaired);
            } else {
                event.setResult(null);
            }
        }
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory anvil) ||
                event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        ItemStack result = event.getCurrentItem();
        if (result == null || !result.hasItemMeta() ||
                !result.getItemMeta().getPersistentDataContainer().has(wateringCanKey, PersistentDataType.BYTE)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 2f, 2f);
        player.getInventory().addItem(result);
        ItemStack rightItem = anvil.getItem(1);
        if (rightItem != null && rightItem.hasItemMeta() &&
                rightItem.getItemMeta().getPersistentDataContainer().has(toolHandleKey, PersistentDataType.BYTE)) {
            int amount = rightItem.getAmount();
            if (amount > 1) {
                rightItem.setAmount(amount - 1);
            } else {
                anvil.setItem(1, null);
            }
        }
        anvil.setItem(0, null);
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasDiscoveredRecipe(new NamespacedKey(plugin, "fts_watering_can"))) {
            player.discoverRecipe(new NamespacedKey(plugin, "fts_watering_can"));
        }

        if (!player.hasDiscoveredRecipe(new NamespacedKey(plugin, "fts_tool_mold"))) {
            player.discoverRecipe(new NamespacedKey(plugin, "fts_tool_mold"));
        }

        if (!player.hasDiscoveredRecipe(new NamespacedKey(plugin, "fts_tool_handle"))) {
            player.discoverRecipe(new NamespacedKey(plugin, "fts_tool_handle"));
        }
    }

    public ItemStack createToolMoldItem() {
        ItemStack item = new ItemStack(Material.NAUTILUS_SHELL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Werkzeug Gussform");
        meta.getPersistentDataContainer().set(toolMoldKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public void registerToolMoldRecipe() {
        ItemStack toolMold = createToolMoldItem();
        NamespacedKey recipeKey = new NamespacedKey(plugin, "fts_tool_mold");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, toolMold);
        recipe.shape(" L ", "LIL", " L ");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('L', Material.LAPIS_LAZULI);
        Bukkit.addRecipe(recipe);
    }

    public ItemStack createToolHandleItem() {
        ItemStack item = new ItemStack(Material.BREEZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Werkzeuggriff");
        meta.getPersistentDataContainer().set(toolHandleKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public void registerToolHandleRecipe() {
        ItemStack toolHandle = createToolHandleItem();
        NamespacedKey recipeKey = new NamespacedKey(plugin, "fts_tool_handle");
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
        }
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, toolHandle);
        recipe.shape(" I ", " I ", " C ");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('C', Material.COPPER_INGOT);
        Bukkit.addRecipe(recipe);
    }

    public ItemStack createWateringCanItem(String uniqueId) {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Gießkanne");
        List<String> lore = new ArrayList<>();
        lore.add("§7Eine spezielle Gießkanne,");
        lore.add("§7um Pflanzen schneller wachsen zu lassen.");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(wateringCanKey, PersistentDataType.BYTE, (byte) 1);
        if (uniqueId != null) {
            meta.getPersistentDataContainer().set(wateringCanIdKey, PersistentDataType.STRING, uniqueId);
        }
        item.setItemMeta(meta);
        return item;
    }

    public void registerWateringCanRecipe() {
        ItemStack wateringCan = createWateringCanItem(null);
        ItemStack toolMold = createToolMoldItem();
        ItemStack toolHandle = createToolHandleItem();
        NamespacedKey recipeKey = new NamespacedKey(plugin, "fts_watering_can");
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
        }
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, wateringCan);
        recipe.shape("  M", " H ", "   ");
        recipe.setIngredient('M', new RecipeChoice.ExactChoice(toolMold));
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(toolHandle));
        Bukkit.addRecipe(recipe);
    }

    public void spawnWateringParticles(Player player) {
        Location eyeLocation = player.getEyeLocation().clone();
        Vector direction = eyeLocation.getDirection().normalize();
        Location spawnLocation = eyeLocation.add(direction.multiply(2));
        World world = spawnLocation.getWorld();
        if (world == null) {
            return;
        }
        world.spawnParticle(Particle.FALLING_WATER, spawnLocation, 40, 0.5, 0.5, 0.5);
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        boolean containsToolHandle = false;
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.hasItemMeta() &&
                    item.getItemMeta().getPersistentDataContainer().has(toolHandleKey, PersistentDataType.BYTE)) {
                containsToolHandle = true;
                break;
            }
        }
        if (!containsToolHandle) {
            return;
        }
        ItemStack result = event.getInventory().getResult();
        if (result == null || !result.hasItemMeta() ||
                !result.getItemMeta().getPersistentDataContainer().has(wateringCanKey, PersistentDataType.BYTE)) {
            event.getInventory().setResult(null);
        }
    }
}
