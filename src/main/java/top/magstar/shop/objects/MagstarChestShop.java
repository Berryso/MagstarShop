package top.magstar.shop.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import top.magstar.economy.objects.Currency;
import top.magstar.shop.Shop;

import java.util.Arrays;
import java.util.UUID;

class MagstarChestShop implements ChestShop{
    private UUID owner;
    private Location location;
    private Currency currency;
    private int price;
    private ItemStack item;
    private ItemMeta meta;
    private double tax;
    private int store;
    private ShopType type;
    private boolean isAdmin;
    private final Item[] item0 = {null};
    private boolean hasRemoved = false;

    public MagstarChestShop(UUID owner, Location location, Currency currency, int price, ItemStack item, double tax, int store, ShopType type, boolean isAdmin) {
        this.owner = owner;
        this.location = location;
        this.currency = currency;
        this.price = price;
        this.item = item;
        this.meta = item.getItemMeta();
        this.tax = tax;
        this.store = store;
        this.type = type;
        this.isAdmin = isAdmin;
    }

    public MagstarChestShop(ChestShop cs) {
        this.owner = cs.getOwner();
        this.location = cs.getLocation();
        this.currency = cs.getCurrency();
        this.price = cs.getPrice();
        this.item = cs.getItem();
        this.meta = cs.getMeta();
        this.tax = cs.getTax();
        this.store = cs.getStore();
        this.type = cs.getType();
        this.isAdmin = cs.isAdmin();
        this.hasRemoved = cs.hasRemoved();
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }
    @Override
    public Location getLocation() {
        return location;
    }
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Item getItemEntity() {
        return item0[0];
    }
    @Override
    public void setHasRemoved(boolean b) {
        hasRemoved = b;
    }
    @Override
    public boolean hasRemoved() {
        return hasRemoved;
    }
    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public double getTax() {
        return tax;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }
    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public void setTax(double tax) {
        this.tax = tax;
    }

    @Override
    public int getStore() {
        return store;
    }

    @Override
    public void setStore(int store) {
        this.store = store;
    }

    @Override
    public ItemMeta getMeta() {
        return meta;
    }

    @Override
    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    @Override
    public ShopType getType() {
        return type;
    }

    @Override
    public void setType(ShopType type) {
        this.type = type;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "{" + owner.toString() + "," + location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + currency.getId() + "," + price + "," + Arrays.toString(item.serializeAsBytes()) + "," + meta.toString() + "," + tax + "," + store + "}";
    }

    @Override
    public void createItem() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (hasRemoved) {
                    cancel();
                } else {
                    ItemStack itemStack = new ItemStack(getItem());
                    itemStack.setAmount(1);
                    Location loc0 = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    Location loc = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
                    loc0.setY(loc0.getBlockY() + 1);
                    loc0.setX(loc0.getBlockX() + 0.5);
                    loc0.setZ(loc0.getBlockZ() + 0.5);
                    if (loc.getBlock().getType() == Material.CAVE_AIR || loc.getBlock().getType() == Material.AIR) {
                        if (item0[0] != null) {
                            item0[0].remove();
                            item0[0] = null;
                        }
                        if (loc.getChunk().isLoaded() && item0[0] == null) {
                            item0[0] = location.getWorld().dropItem(loc0, itemStack, item -> {
                                item.setCanMobPickup(false);
                                item.setCanPlayerPickup(false);
                                item.setWillAge(false);
                                item.setUnlimitedLifetime(true);
                                item.setGravity(false);
                                item.setInvulnerable(true);
                                item.setVelocity(new Vector(0, 0, 0));
                                item.setOwner(owner);
                            });
                        }
                    } else {
                        if (item0[0] != null) {
                            item0[0].setHealth(-1);
                            item0[0] = null;
                        }
                    }
                }
            }
        }.runTaskTimer(Shop.getInstance(), 0, 1200);
    }
    @Override
    public void removeItem() {
        if (item0[0] != null) {
            item0[0].setHealth(-1);
            item0[0].remove();
            item0[0] = null;
        }
    }
    @Override
    public boolean equals(Object cs) {
        if (cs instanceof ChestShop shop) {
            return location.equals(shop.getLocation());
        } else {
            return false;
        }
    }
}
