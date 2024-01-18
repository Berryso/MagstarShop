package top.magstar.shop.utils.fileutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.shop.Shop;

import java.io.File;

public final class Trade {
    private static File f = new File(Shop.getInstance().getDataFolder() + "/trade.yml");
    public static void reload() {
        if (!f.exists()) {
            Shop.getInstance().saveResource("trade.yml", false);
        }
        f = new File(Shop.getInstance().getDataFolder() + "/trade.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
}
