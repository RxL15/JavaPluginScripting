package FtsCraft.fTSWateringCan;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.RED;

public class BackpackListener implements Listener {

    private final BackpackManager backpackManager;

    public BackpackListener(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackpackHolder)) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        Player player = (Player) event.getWhoClicked();
        if (clickedItem != null && clickedItem.getType() == Material.BONE_MEAL || cursorItem.getType() == Material.BONE_MEAL) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(RED + "Nur Dünger ist erlaubt!");
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BackpackHolder) {
            Player player = (Player) event.getWhoClicked();
            for (ItemStack item : event.getNewItems().values()) {
                if (item != null && item.getType() != Material.BONE_MEAL) {
                    event.setCancelled(true);
                    player.sendMessage(RED + "Nur Dünger ist erlaubt!");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BackpackHolder backpackHolder) {
            String uniqueId = backpackHolder.getUniqueId();
            backpackManager.setBackpack(uniqueId, event.getInventory().getContents());
        }
    }
}

