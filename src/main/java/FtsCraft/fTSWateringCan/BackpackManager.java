package FtsCraft.fTSWateringCan;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackManager {

    private final JavaPlugin plugin;
    private final Map<String, ItemStack[]> backpacks = new HashMap<>();
    private File backpackFile;
    private FileConfiguration backpackConfig;
    public BackpackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadBackpacks();
    }

    public void loadBackpacks() {
        backpackFile = new File(plugin.getDataFolder(), "fertilizer-bags.yml");
        if (!backpackFile.exists()) {
            backpackFile.getParentFile().mkdirs();
            try {
                backpackFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        backpackConfig = YamlConfiguration.loadConfiguration(backpackFile);
        if (backpackConfig.contains("backpacks")) {
            ConfigurationSection section = backpackConfig.getConfigurationSection("backpacks");
            for (String key : section.getKeys(false)) {
                java.util.List<ItemStack> list = (java.util.List<ItemStack>) section.getList(key);
                ItemStack[] items = new ItemStack[9];
                for (int i = 0; i < Math.min(items.length, list.size()); i++) {
                    items[i] = list.get(i);
                }
                backpacks.put(key, items);
            }
        }
    }

    public void saveBackpacks() {
        if (backpackConfig == null) {
            backpackConfig = new YamlConfiguration();
        }
        ConfigurationSection section = backpackConfig.createSection("backpacks");
        for (Map.Entry<String, ItemStack[]> entry : backpacks.entrySet()) {
            section.set(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        try {
            backpackConfig.save(backpackFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBackpack(String uniqueId, ItemStack[] contents) {
        backpacks.put(uniqueId, contents);
    }

    public void openBackpack(Player player, String uniqueId) {
        ItemStack[] content = backpacks.get(uniqueId);
        if (content == null) {
            content = new ItemStack[9];
            backpacks.put(uniqueId, content);
        }
        Inventory inv = Bukkit.createInventory(new BackpackHolder(uniqueId), 9, "§2Dünger Beutel");
        inv.setContents(content);
        player.openInventory(inv);
    }

    public boolean useBoneMeal(String uniqueId) {
        ItemStack[] inv = backpacks.get(uniqueId);
        if (inv == null)
            return false;
        for (int i = 0; i < inv.length; i++) {
            ItemStack stack = inv[i];
            if (stack != null && stack.getType() == Material.BONE_MEAL && stack.getAmount() > 0) {
                stack.setAmount(stack.getAmount() - 1);
                if (stack.getAmount() <= 0) {
                    inv[i] = null;
                }
                backpacks.put(uniqueId, inv);
                return true;
            }
        }
        return false;
    }
}
