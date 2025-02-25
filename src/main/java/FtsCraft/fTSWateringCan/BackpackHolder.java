package FtsCraft.fTSWateringCan;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BackpackHolder implements InventoryHolder {

    private final String uniqueId;

    public BackpackHolder(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

