package top.magstar.shop.objects;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.magstar.economy.objects.Currency;

import java.util.UUID;

public interface ChestShop {
    void setPrice(int price);
    Location getLocation();
    void setLocation(Location location);
    Item getItemEntity();
    void setHasRemoved(boolean b);
    boolean hasRemoved();
    Currency getCurrency();
    int getPrice();
    ItemStack getItem();
    double getTax();
    UUID getOwner();
    void setOwner(UUID owner);
    void setCurrency(Currency currency);
    void setItem(ItemStack item);
    void setTax(double tax);
    int getStore();
    void setStore(int store);
    ItemMeta getMeta();
    void setMeta(ItemMeta meta);
    ShopType getType();
    void setType(ShopType type);
    boolean isAdmin();
    void setAdmin(boolean admin);
    void createItem();
    void removeItem();
}
