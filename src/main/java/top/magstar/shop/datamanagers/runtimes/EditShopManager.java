package top.magstar.shop.datamanagers.runtimes;

import org.bukkit.entity.Player;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ChestShopFactory;

import java.util.ArrayList;
import java.util.List;

public class EditShopManager {
    private static final List<ChestShop> changePrice = new ArrayList<>();
    public synchronized static void addShop(ChestShop cs) {
        changePrice.add(cs);
    }
    public synchronized static ChestShop getShop(Player p) {
        for (ChestShop cs : changePrice) {
            if (cs.getOwner().toString().equals(p.getUniqueId().toString())) {
                return cs;
            }
        }
        return null;
    }
    public synchronized static List<ChestShop> getAllShops() {
        return changePrice;
    }
    public synchronized static void saveData(Player p) {
        ChestShop cs = ChestShopFactory.buildChestShop(getShop(p));
        RuntimeDataManager.removeShop(getShop(p));
        RuntimeDataManager.loadData(cs);
        changePrice.remove(getShop(p));
    }
    public synchronized static void removeShop(Player p) {
        changePrice.remove(getShop(p));
    }
    public synchronized static void removeAll() {
        changePrice.removeAll(changePrice);
    }
    public synchronized static boolean containShop(ChestShop cs) {
        for (ChestShop shop : changePrice) {
            if (shop.getLocation().equals(cs.getLocation())) {
                return true;
            }
        }
        return false;
    }
}
