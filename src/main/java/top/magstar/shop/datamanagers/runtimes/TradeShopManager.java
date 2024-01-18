package top.magstar.shop.datamanagers.runtimes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TradeShopManager {
    private final static Map<Player, ChestShop> TRADE_MAP = new HashMap<>();

    public synchronized static void addPlayer(Player p, ChestShop cs) {
        TRADE_MAP.put(p, cs);
    }
    public synchronized static void saveShop(ChestShop cs) {
        for (ChestShop shop : RuntimeDataManager.getShops(cs.getOwner())) {
            if (cs.getLocation().getWorld().getName().equals(shop.getLocation().getWorld().getName()) &&
            cs.getLocation().getBlockX() == shop.getLocation().getBlockX() &&
            cs.getLocation().getBlockY() == shop.getLocation().getBlockY() &&
            cs.getLocation().getBlockZ() == shop.getLocation().getBlockZ() ) {
                Objects.requireNonNull(RuntimeDataManager.getShop(shop.getLocation())).setStore(cs.getStore());
            }
        }
    }
    public synchronized static void removePlayer(Player p) {
        TRADE_MAP.remove(p);
    }
    public synchronized static boolean isShopAccessing(ChestShop cs) {
        return TRADE_MAP.containsValue(cs);
    }
    public synchronized static boolean isPlayerTrading(Player p) {
        return TRADE_MAP.containsKey(p);
    }
    public synchronized static ChestShop getAccessingShop(Player p) {
        return TRADE_MAP.get(p);
    }
    public synchronized static int getInventoryItemCount(Player p) {
        ChestShop cs = TRADE_MAP.get(p);
        ItemStack item = new ItemStack(cs.getItem());
        item.setItemMeta(cs.getMeta());
        int sum = 0;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null) {
                if (GeneralUtils.isItemStackSame(i, item)) {
                    sum += i.getAmount();
                }
            }
        }
        return sum;
    }
    public synchronized static void removeAll() {
        TRADE_MAP.clear();
    }
    public synchronized static int getShopVolume(ChestShop cs) {
        return top.magstar.economy.datamanagers.AbstractDataManager.getDataManagerInstance().getPlayerCoin(cs.getOwner(), cs.getCurrency().getId()) / cs.getPrice();
    }
    public synchronized static boolean containShop(ChestShop cs) {
        for (ChestShop shop : TRADE_MAP.values()) {
            if (shop.getLocation().equals(cs.getLocation())) {
                return true;
            }
        }
        return false;
    }
}
