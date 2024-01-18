package top.magstar.shop.datamanagers.runtimes;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.utils.GeneralUtils;
import top.magstar.shop.utils.fileutils.ShopList;
import top.magstar.shop.utils.fileutils.Viewer;

import java.util.*;

public class RuntimeDataManager {
    private static final List<ChestShop> SHOP_LIST = new ArrayList<>();
    private static final Map<String, Map<Integer, ItemStack>> ITEM_MAP = new HashMap<>();

    public synchronized static void loadData(ChestShop cs) {
        SHOP_LIST.add(cs);
        if (cs.getItemEntity() == null) {
            cs.createItem();
        } else {
            cs.removeItem();
            cs.createItem();
        }
    }
    public synchronized static void loadDataSilently(ChestShop cs) {
        SHOP_LIST.add(cs);
    }
    public synchronized static void loadItem(String p, Map<Integer, ItemStack> map) {
        ITEM_MAP.put(p, map);
    }
    public synchronized static int containsPlayerItem(String p, ItemStack i) {
        if (ITEM_MAP.containsKey(p)) {
            for (int id = 1; id <= ITEM_MAP.get(p).size(); id++) {
                if (GeneralUtils.isItemStackSame(ITEM_MAP.get(p).get(id) ,i)) {
                    return id;
                }
            }
        }
        return -1;
    }
    public synchronized static void selfUpdate() {
        for (String uuid : ITEM_MAP.keySet()) {
            int id = 1;
            List<ItemStack> items = ITEM_MAP.get(uuid).values().stream().toList();
            Map<Integer, ItemStack> map = new HashMap<>();
            for (ItemStack item : items) {
                map.put(id, item);
                id++;
            }
            ITEM_MAP.put(uuid, map);
        }
    }
    public synchronized static void addPlayerItem(String p, ItemStack i) {
        if (i.getType() != Material.AIR) {
            int id = containsPlayerItem(p, i);
            if (ITEM_MAP.containsKey(p)) {
                if (id != -1) {
                    setItemAmount(p, id, ITEM_MAP.get(p).get(id).getAmount() + i.getAmount());
                }
                else {
                    ITEM_MAP.get(p).put(ITEM_MAP.get(p).size() + 1, i);
                }
            } else {
                ITEM_MAP.put(p, new HashMap<>());
                ITEM_MAP.get(p).put(1, i);
            }
        }
    }
    public synchronized static void removeItemEntityInChunk(Chunk a) {
        for (ChestShop cs : SHOP_LIST) {
            if (GeneralUtils.isChunkSame(a, cs.getLocation().getChunk())) {
                cs.setHasRemoved(true);
                cs.removeItem();
            }
        }
    }
    public synchronized static void loadItemEntityInChunk(Chunk a) {
        for (ChestShop cs : SHOP_LIST) {
            if (GeneralUtils.isChunkSame(a, cs.getLocation().getChunk())) {
                cs.setHasRemoved(false);
                cs.createItem();
            }
        }
    }
    public synchronized static Map<Integer, ItemStack> getItems(String p) {
        return ITEM_MAP.get(p);
    }
    public synchronized static void deletePlayer(String p) {
        ITEM_MAP.remove(p);
    }
    public synchronized static void setItemAmount(String p, int id, int count) {
        ITEM_MAP.get(p).get(id).setAmount(count);
    }
    public synchronized static void deleteItem(String p, int id) {
        ITEM_MAP.get(p).remove(id);
        selfUpdate();
    }
    public synchronized static void removeAllItems() {
        ITEM_MAP.clear();
    }
    public synchronized static Map<String, Map<Integer, ItemStack>> getItemMap() {
        return ITEM_MAP;
    }
    public synchronized static List<ChestShop> getShops(UUID uuid) {
        List<ChestShop> list = new ArrayList<>();
        for (ChestShop cs : SHOP_LIST) {
            if (cs.getOwner().toString().equals(uuid.toString())) {
                list.add(cs);
            }
        }
        return list;
    }
    public synchronized static boolean isItemFromChestShop(Item item) {
        boolean b = false;
        for (ChestShop cs : SHOP_LIST) {
            ItemStack stack = new ItemStack(cs.getItem());
            stack.setItemMeta(cs.getMeta());
            if (!item.canPlayerPickup() && !item.canMobPickup() && !item.willAge() && !item.hasGravity() && GeneralUtils.isItemStackSame(item.getItemStack(), stack)) {
                b = true;
                break;
            }
        }
        return b;
    }
    public synchronized static List<ChestShop> getAllShops() {
        return SHOP_LIST;
    }
    public synchronized static void removeShop(ChestShop cs) {
        SHOP_LIST.remove(cs);
        cs.removeItem();
        cs.setHasRemoved(true);
    }
    public synchronized static void removeShopByLocation(ChestShop cs) {
        SHOP_LIST.removeIf(chestShop -> chestShop.getLocation().equals(cs.getLocation()));
    }
    public synchronized static void removeAll() {
        for (ChestShop cs : SHOP_LIST) {
            cs.removeItem();
            cs.setHasRemoved(true);
        }
        SHOP_LIST.removeAll(SHOP_LIST);
    }
    public synchronized static boolean isChestShop(Location loc) {
        for (ChestShop cs : SHOP_LIST) {
            if (
                    cs.getLocation().getWorld().getName().equals(loc.getWorld().getName()) &&
                            cs.getLocation().getBlockX() == loc.getBlockX() &&
                            cs.getLocation().getBlockY() == loc.getBlockY() &&
                            cs.getLocation().getBlockZ() == loc.getBlockZ()
            ) {
                return true;
            }
        }
        return false;
    }
    public synchronized static ChestShop getShop(Location loc) {
        for (ChestShop cs : SHOP_LIST) {
            if (
                    cs.getLocation().getWorld().getName().equals(loc.getWorld().getName()) &&
                            cs.getLocation().getBlockX() == loc.getBlockX() &&
                            cs.getLocation().getBlockY() == loc.getBlockY() &&
                            cs.getLocation().getBlockZ() == loc.getBlockZ()
            ) {
                return cs;
            }
        }
        return null;
    }
    public synchronized static int calculateViewerAllPage(Player p) {
        int size = Viewer.getConfig().getIntegerList("Buttons.<available_locations>").size();
        int block = 0;
        if (ITEM_MAP.get(p.getUniqueId().toString()) != null) {
            for (Map.Entry<Integer, ItemStack> entry : ITEM_MAP.get(p.getUniqueId().toString()).entrySet()) {
                int amount = entry.getValue().getAmount();
                int max = entry.getValue().getMaxStackSize();
                block += Math.ceil(((double) amount) / max);
            }
            return (int) Math.ceil(((double)block) / size);
        } else {
            return 1;
        }
    }
    public synchronized static int calculateShopListAllPage(Player p) {
        int size = ShopList.getConfig().getIntegerList("Buttons.<chest_shops>.Location").size();
        if (getShops(p.getUniqueId()).size() == 0) {
            return 1;
        } else {
            return (int) Math.ceil(((double) getShops(p.getUniqueId()).size()) / size);
        }
    }
}
