package top.magstar.shop.datamanagers.runtimes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ChestShopFactory;
import top.magstar.shop.objects.ShopType;

import java.util.*;

public class RuntimeManager {
    private static final List<ChestShop> chestList = new ArrayList<>();

    public synchronized static boolean isMapContains(Player p) {
        for (ChestShop cs : chestList) {
            if (cs.getOwner().toString().equals(p.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
    public synchronized static ChestShop getChestShop(Player p) {
        for (ChestShop cs : chestList) {
            if (cs.getOwner().toString().equals(p.getUniqueId().toString())) {
                return cs;
            }
        }
        return null;
    }
    public synchronized static void changeShop(Player p, ChestShop shop) {
        if (shop.getOwner().toString().equals(p.getUniqueId().toString())) {
            removePlayer(p);
            chestList.add(shop);
        }
    }
    public synchronized static void addPlayer(Player p, Location c) {
        ItemStack i = p.getInventory().getItemInMainHand();
        ItemStack i1 = new ItemStack(i);
        i1.setAmount(1);
        chestList.add(ChestShopFactory.buildChestShop(p.getUniqueId(), c, null, 0, i1, 0, 0, ShopType.sale, false));
    }
    public synchronized static void removePlayer(Player p) {
        chestList.removeIf(cs -> cs.getOwner().toString().equals(p.getUniqueId().toString()));
    }
    public synchronized static void removeAll() {
        chestList.removeAll(chestList);
    }
}
