package FtsCraft.fTSWateringCan;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WateringCanListener implements Listener {

    private final FTSWateringCan plugin;
    private final WateringCanManager wateringCanManager;
    private final BackpackManager backpackManager;

    public WateringCanListener(FTSWateringCan plugin, WateringCanManager wateringCanManager, BackpackManager backpackManager) {
        this.plugin = plugin;
        this.wateringCanManager = wateringCanManager;
        this.backpackManager = backpackManager;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType().isAir()) return;
        if (result.hasItemMeta() && result.getItemMeta().getDisplayName().equals("§9Gießkanne")) {
            ItemMeta meta = result.getItemMeta();
            if (!meta.getPersistentDataContainer().has(wateringCanManager.getWateringCanIdKey(), PersistentDataType.STRING)) {
                String uniqueId = wateringCanManager.generateUniqueId();
                meta.getPersistentDataContainer().set(wateringCanManager.getWateringCanIdKey(), PersistentDataType.STRING, uniqueId);
                result.setItemMeta(meta);
                backpackManager.setBackpack(uniqueId, new ItemStack[9]);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(wateringCanManager.getWateringCanIdKey(), PersistentDataType.STRING))
            return;
        String uniqueId = meta.getPersistentDataContainer().get(wateringCanManager.getWateringCanIdKey(), PersistentDataType.STRING);
        if (uniqueId == null)
            return;
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (player.isSneaking()) {
            backpackManager.openBackpack(player, uniqueId);
            return;
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null) {
                if (block.getBlockData() instanceof Ageable ageable) {
                    if (ageable.getAge() < ageable.getMaximumAge()) {
                        if (backpackManager.useBoneMeal(uniqueId)) {
                            ageable.setAge(ageable.getMaximumAge());
                            block.setBlockData(ageable);
                            player.playSound(block.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);
                            wateringCanManager.spawnWateringParticles(player);
                            ItemStack itemInHand = player.getInventory().getItemInMainHand();
                            ItemMeta itemMeta = itemInHand.getItemMeta();
                            if (itemMeta instanceof Damageable damageable) {
                                int newDamage = damageable.getDamage() + 1;
                                int maxDurability = itemInHand.getType().getMaxDurability();

                                if (newDamage >= maxDurability) {
                                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                } else {
                                    damageable.setDamage(newDamage);
                                    itemInHand.setItemMeta((ItemMeta) damageable);
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Dein Beutel hat kein Dünger mehr!");
                        }
                    }
                }
            }
        }
    }
}


