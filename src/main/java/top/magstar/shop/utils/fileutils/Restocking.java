package top.magstar.shop.utils.fileutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.shop.Shop;

import java.io.File;

public final class Restocking {
    private static File f = new File(Shop.getInstance().getDataFolder() + "/restocking.yml");
    public static void reload() {
        if (!f.exists()) {
            Shop.getInstance().saveResource("restocking.yml", false);
        }
        f = new File(Shop.getInstance().getDataFolder() + "/restocking.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
}
