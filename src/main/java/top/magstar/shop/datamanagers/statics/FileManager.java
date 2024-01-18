package top.magstar.shop.datamanagers.statics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import top.magstar.economy.objects.Currency;
import top.magstar.economy.objects.CurrencyManager;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.runtimes.RuntimeDataManager;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ChestShopFactory;
import top.magstar.shop.objects.ShopType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileManager extends AbstractDataManager{
    private static File f = new File(Shop.getInstance().getDataFolder() + "/chestshops.yml");
    private static File f0 = new File(Shop.getInstance().getDataFolder() + "/items.yml");
    @Override
    public void reload() {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!f0.exists()) {
            try {
                f0.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        f = new File(Shop.getInstance().getDataFolder() + "/chestshops.yml");
        f0 = new File(Shop.getInstance().getDataFolder() + "/items.yml");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPluginManager().isPluginEnabled("MagstarEconomy")) {
                    loadData();
                    cancel();
                }
            }
        }.runTaskTimer(Shop.getInstance(), 0, 20L);
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
    @Override
    public void loadData() {
        RuntimeDataManager.removeAll();
        Set<String> uuidSet = getConfig().getKeys(false);
        for (String uuid : uuidSet) {
            Set<String> locations = Objects.requireNonNull(getConfig().getConfigurationSection(uuid)).getKeys(false);
            for (String loc : locations) {
                String[] args = loc.split("\\|");
                Location location = new Location(Bukkit.getWorld(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                Currency c = CurrencyManager.byName(getConfig().getString(uuid + "." + loc + ".Currency"));
                ItemStack item = getConfig().getItemStack(uuid + "." + loc + ".Item");
                int price = getConfig().getInt(uuid + "." + loc + ".Price");
                double tax = getConfig().getDouble(uuid + "." + loc + ".Tax");
                int store = getConfig().getInt(uuid + "." + loc + ".Store");
                ShopType type = ShopType.getType(Objects.requireNonNull(getConfig().getString(uuid + "." + loc + ".Type")));
                boolean b = getConfig().getBoolean(uuid + "." + loc + ".isAdmin");
                RuntimeDataManager.loadData(ChestShopFactory.buildChestShop(UUID.fromString(uuid), location, c, price, item, tax, store, type, b));
            }
        }
    }
    @Override
    public void saveData() {
        List<ChestShop> shops = RuntimeDataManager.getAllShops();
        FileConfiguration cfg = new YamlConfiguration();
        for (ChestShop cs : shops) {
            String location = cs.getLocation().getWorld().getName() + "|" +  cs.getLocation().getBlockX() + "|" + cs.getLocation().getBlockY() + "|" + cs.getLocation().getBlockZ();
            cfg.set(cs.getOwner() + "." + location + ".Currency", cs.getCurrency().getId());
            cfg.set(cs.getOwner() + "." + location + ".Price", cs.getPrice());
            ItemStack item = cs.getItem();
            item.setItemMeta(cs.getMeta());
            cfg.set(cs.getOwner() + "." + location + ".Item", item);
            cfg.set(cs.getOwner() + "." + location + ".Tax", cs.getTax());
            cfg.set(cs.getOwner() + "." + location + ".Store", cs.getStore());
            cfg.set(cs.getOwner() + "." + location + ".Type", cs.getType());
            cfg.set(cs.getOwner() + "." + location + ".isAdmin", cs.isAdmin());
        }
        try {
            cfg.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteData(ChestShop cs) {}

    @Override
    public void loadItems() {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f0);
        for (String uuid : cfg.getKeys(false)) {
            Map<Integer, ItemStack> items = new HashMap<>();
            int count = cfg.getConfigurationSection(uuid).getKeys(false).size();
            for (int i = 1; i <= count; i++) {
                ItemStack item = cfg.getItemStack(uuid + "." + i + ".Item");
                items.put(i, item);
            }
            RuntimeDataManager.loadItem(uuid, items);
        }
    }

    @Override
    public void saveItems() {
        Map<String, Map<Integer, ItemStack>> map = RuntimeDataManager.getItemMap();
        FileConfiguration cfg = new YamlConfiguration();
        for (String p : map.keySet()) {
            for (Map.Entry<Integer, ItemStack> entry : map.get(p).entrySet()) {
                cfg.set(p + "." + entry.getKey() + ".Item", entry.getKey());
            }
        }
        try {
            cfg.save(f0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteItems(Player p, int id) {}
}
