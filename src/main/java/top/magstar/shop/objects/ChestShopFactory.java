package top.magstar.shop.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import top.magstar.economy.objects.Currency;

import java.util.UUID;

public class ChestShopFactory {
    private ChestShopFactory() {}
    public static ChestShop buildChestShop(UUID owner, Location location, Currency currency, int price, ItemStack item, double tax, int store, ShopType type, boolean isAdmin) {
        return new MagstarChestShop(owner, location, currency, price, item, tax, store, type, isAdmin);
    }
    public static ChestShop buildChestShop(ChestShop cs) {
        return new MagstarChestShop(cs);
    }
}
