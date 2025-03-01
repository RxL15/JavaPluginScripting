package FtsCraft.fTSWateringCan;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class FTSWateringCan extends JavaPlugin {

    private WateringCanManager wateringCanManager;
    private BackpackManager backpackManager;

    @Override
    public void onEnable() {
        wateringCanManager = new WateringCanManager(this);
        backpackManager = new BackpackManager(this);

        getServer().getPluginManager().registerEvents(new WateringCanListener(wateringCanManager, backpackManager), this);
        getServer().getPluginManager().registerEvents(new BackpackListener(backpackManager), this);
        Bukkit.getPluginManager().registerEvents(wateringCanManager, this);

        wateringCanManager.registerToolMoldRecipe();
        wateringCanManager.registerToolHandleRecipe();
        wateringCanManager.registerWateringCanRecipe();
    }

    @Override
    public void onDisable() {
        backpackManager.saveBackpacks();
    }

    {
        HandlerList.unregisterAll(this);
    }
}
