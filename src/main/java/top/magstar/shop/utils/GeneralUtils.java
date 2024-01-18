package top.magstar.shop.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.runtimes.*;
import top.magstar.shop.datamanagers.statics.AbstractDataManager;
import top.magstar.shop.handlers.CommandHandler;
import top.magstar.shop.objects.ButtonType;
import top.magstar.shop.objects.Schedule;
import top.magstar.shop.utils.fileutils.*;
import xyz.magstar.lib.api.IDataManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.*;

public final class GeneralUtils {
    public static boolean isPrePluginLoaded() {
        return Bukkit.getPluginManager().getPlugin("MagstarLib") != null && Bukkit.getPluginManager().getPlugin("MagstarEconomy") != null;
    }
    public static boolean isLoginSuccessful() {
        IDataManager iDataManager = Bukkit.getServicesManager().load(IDataManager.class);
        assert iDataManager != null;
        return iDataManager.check(Shop.getSerialNumber(), Shop.getUserVersion());
    }
    public static void init() {
        Shop.dbAvailable = ConfigUtils.getConfig().getBoolean("mysql.enabled");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPluginManager().isPluginEnabled("MagstarEconomy")) {
                    top.magstar.shop.datamanagers.statics.IDataManager idm = AbstractDataManager.getInstance();
                    idm.reload();
                    idm.loadData();
                    idm.loadItems();
                    cancel();
                }
            }
        }.runTaskTimer(Shop.getInstance(), 0, 20L);
        ConfigUtils.reload();
        Message.reload();
        ShopInfo.reload();
        Restocking.reload();
        Schedule.saveData();
        Viewer.reload();
        Trade.reload();
        ShopList.reload();
        Objects.requireNonNull(Bukkit.getPluginCommand("MagstarShop")).setExecutor(new CommandHandler());
        Shop.getInstance().getLogger().info("         §a>>> 注册完成 <<<");
    }
    public static String getTranslate(String s) {
        return s.replace("&", "§");
    }
    public static byte[] blobToBytes(Blob blob) {
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(blob.getBinaryStream());
            byte[] bytes = new byte[(int)blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;
            while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return null;
            }
        }
    }
    public static ButtonType getType(Map<ButtonType, Integer> map, int i) {
        for (Map.Entry<ButtonType, Integer> entry : map.entrySet()) {
            if (entry.getValue() == i) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static boolean isItemStackSame(ItemStack a, ItemStack b) {
        boolean name;
        if (a.hasItemMeta() && b.hasItemMeta()) {
            if (a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName()) {
                name = a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
            } else {
                name = (!a.getItemMeta().hasDisplayName() || b.getItemMeta().hasDisplayName()) && (a.getItemMeta().hasDisplayName() || !b.getItemMeta().hasDisplayName());
            }
        } else {
            name = (!a.hasItemMeta() || b.hasItemMeta()) && (a.hasItemMeta() || !b.hasItemMeta());
        }
        boolean lore = true;
        if (a.hasItemMeta() && b.hasItemMeta()) {
            if (a.getItemMeta().hasLore() && b.getItemMeta().hasLore()) {
                if (a.getLore().size() == b.getLore().size()) {
                    for (int i = 0; i < a.getLore().size(); i++) {
                        if (!a.getLore().get(i).equals(b.getLore().get(i))) {
                            lore = false;
                        }
                    }
                } else {
                    lore = false;
                }
            } else {
                if ((!a.getItemMeta().hasLore() && b.getItemMeta().hasLore()) || (a.getItemMeta().hasLore() && !b.getItemMeta().hasLore())) {
                    lore = false;
                }
            }
        } else {
            lore = (!a.hasItemMeta() || b.hasItemMeta()) && (a.hasItemMeta() || !b.hasItemMeta());
        }
        boolean enchant = true;
        if (!a.getEnchantments().isEmpty() && !b.getEnchantments().isEmpty()) {
            if (a.getEnchantments().size() == b.getEnchantments().size()) {
                for (Map.Entry<Enchantment, Integer> entry : a.getEnchantments().entrySet()) {
                    if (b.getEnchantmentLevel(entry.getKey()) != entry.getValue()) {
                        enchant = false;
                    }
                }
            } else {
                enchant = false;
            }
        } else {
            if ((!a.getEnchantments().isEmpty() && b.getEnchantments().isEmpty()) || (a.getEnchantments().isEmpty() && !b.getEnchantments().isEmpty())) {
                enchant = false;
            }
        }
        boolean type = a.getType().equals(b.getType());
        boolean durability;
        if (a.hasItemMeta() && b.hasItemMeta()) {
            if (a.getItemMeta() instanceof Damageable && b.getItemMeta() instanceof Damageable) {
                durability = ((Damageable) a.getItemMeta()).getDamage() == ((Damageable) b.getItemMeta()).getDamage();
            } else {
                if ((!(a.getItemMeta() instanceof Damageable) && (b.getItemMeta() instanceof Damageable)) || ((a.getItemMeta() instanceof Damageable) && !(b.getItemMeta() instanceof Damageable))) {
                    durability = false;
                } else {
                    durability = true;
                }
            }
        } else {
            durability = (!a.hasItemMeta() || b.hasItemMeta()) && (a.hasItemMeta() || !b.hasItemMeta());
        }
        return name && lore && enchant && type && durability;
    }
    public static void flushRuntimeData() {
        RuntimeDataManager.removeAll();
        RuntimeDataManager.removeAllItems();
        RuntimeManager.removeAll();
        RuntimeViewerManager.removeAll();
        EditShopManager.removeAll();
        TradeShopManager.removeAll();
        ShopListManager.removeAll();
    }
    public static List<Location> isChestNear(Location loc) {
        List<Location> list = new ArrayList<>();
        if ((new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ())).getBlock().getType() == Material.CHEST && !((new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ())).getBlock() instanceof DoubleChest)) {
            list.add(new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ()));
        }
        if ((new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ())).getBlock().getType() == Material.CHEST && !((new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ())).getBlock() instanceof DoubleChest)) {
            list.add(new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()));
        }
        if ((new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1)).getBlock().getType() == Material.CHEST && !((new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1)).getBlock() instanceof DoubleChest)) {
            list.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1));
        }
        if ((new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1)).getBlock().getType() == Material.CHEST && !((new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1)).getBlock() instanceof DoubleChest)) {
            list.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1));
        }
        return list;
    }
    public static boolean isChunkSame(Chunk a, Chunk b) {
        return a.getWorld().getName().equals(b.getWorld().getName()) &&
                a.getX() == b.getX() &&
                a.getZ() == b.getZ();
    }
}
