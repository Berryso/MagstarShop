package top.magstar.shop.datamanagers.statics;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.magstar.economy.objects.Currency;
import top.magstar.economy.objects.CurrencyManager;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.runtimes.RuntimeDataManager;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ChestShopFactory;
import top.magstar.shop.objects.ShopType;
import top.magstar.shop.utils.GeneralUtils;
import top.magstar.shop.utils.fileutils.ConfigUtils;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.*;

public class SQLManager extends AbstractDataManager{
    private static String dbUrl, username, password;
    @Override
    public void reload() {
        Configuration cfg = ConfigUtils.getConfig();
        String host = cfg.getString("mysql.host");
        String dbName = cfg.getString("mysql.db-name");
        username = cfg.getString("mysql.username");
        password = cfg.getString("mysql.password");
        String port = cfg.getString("mysql.port");
        if (host == null || dbName == null || port == null) {
            Shop.dbAvailable = false;
            throw new InvalidParameterException("MySQL Configuration not complete!");
        }
        dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS magstar_shop_data (" +
                            "world VARCHAR(255) NOT NULL, " +
                            "x INTEGER NOT NULL, " +
                            "y INTEGER NOT NULL, " +
                            "z INTEGER NOT NULL, " +
                            "uuid VARCHAR(36) NOT NULL, " +
                            "item BLOB NOT NULL, " +
                            "nbt LONGTEXT NOT NULL, " +
                            "id TEXT NOT NULL, " +
                            "price INTEGER NOT NULL, " +
                            "tax DOUBLE NOT NULL, " +
                            "store INTEGER NOT NULL, " +
                            "sale BOOLEAN NOT NULL, " +
                            "admin BOOLEAN NOT NULL" +
                            ")"
            );
            ps.execute();
            ps.close();
            PreparedStatement ps0 = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS magstar_item_data (" +
                            "uuid VARCHAR(255) NOT NULL, " +
                            "id INTEGER NOT NULL, " +
                            "item BLOB NOT NULL, " +
                            "amount INTEGER NOT NULL" +
                            ")"
            );
            ps0.execute();
            ps0.close();
            conn.close();
            Shop.getInstance().getLogger().info("已成功与数据库取得连接。");
        } catch (ClassNotFoundException e) {
            Shop.getInstance().getLogger().info("未发现JDBC前置库。请重装本插件。");
            Shop.dbAvailable = false;
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Shop.getInstance());
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void loadData() {
        RuntimeDataManager.removeAll();
        try {
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement("SELECT world, x, y, z, uuid, item, nbt, id, price, tax, store, sale, admin FROM magstar_shop_data");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String worldName = rs.getString(1);
                int x = rs.getInt(2);
                int y = rs.getInt(3);
                int z = rs.getInt(4);
                String owner = rs.getString(5);
                byte[] data = GeneralUtils.blobToBytes(rs.getBlob(6));
                ItemStack item = ItemStack.deserializeBytes(data);
                Map<String, String> nbtMap = executeNBTString(rs.getString(7));
                Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                UUID uuid = UUID.fromString(owner);
                Currency currency = CurrencyManager.byName(rs.getString(8));
                int price = rs.getInt(9);
                double tax = rs.getDouble(10);
                int store = rs.getInt(11);
                ShopType type = rs.getBoolean(12) ? ShopType.sale : ShopType.purchase;
                boolean isAdmin = rs.getBoolean(13);
                ChestShop cs = ChestShopFactory.buildChestShop(uuid, loc, currency, price, item, tax, store, type, isAdmin);
                RuntimeDataManager.loadData(cs);
            }
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void saveData() {
        List<ChestShop> shops = RuntimeDataManager.getAllShops();
        for (ChestShop cs : shops) {
            try {
                Connection con = DriverManager.getConnection(dbUrl, username, password);
                PreparedStatement ps0 = con.prepareStatement(
                        "SELECT world FROM magstar_shop_data WHERE (x=?) AND (y=?) AND (z=?) AND (world=?)"
                );
                ps0.setInt(1, cs.getLocation().getBlockX());
                ps0.setInt(2, cs.getLocation().getBlockY());
                ps0.setInt(3, cs.getLocation().getBlockZ());
                ps0.setString(4, cs.getLocation().getWorld().getName());
                ResultSet rs = ps0.executeQuery();
                if (!rs.next()) {
                    if (rs.getRow() == 0) {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO magstar_shop_data (world, x, y, z, uuid, item, nbt, id, price, tax, store, sale, admin) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        );
                        ps.setString(1, cs.getLocation().getWorld().getName());
                        ps.setInt(2, cs.getLocation().getBlockX());
                        ps.setInt(3, cs.getLocation().getBlockY());
                        ps.setInt(4, cs.getLocation().getBlockZ());
                        ps.setString(5, cs.getOwner().toString());
                        ItemStack item = new ItemStack(cs.getItem());
                        item.setItemMeta(cs.getMeta());
                        byte[] data = item.serializeAsBytes();
                        Blob blob = con.createBlob();
                        blob.setBytes(1, data);
                        ps.setBlob(6, blob);
                        ps.setString(7, getNBTString(cs.getItem()));
                        ps.setString(8, cs.getCurrency().getId());
                        ps.setInt(9, cs.getPrice());
                        ps.setDouble(10, cs.getTax());
                        ps.setInt(11, cs.getStore());
                        ps.setBoolean(12, cs.getType() == ShopType.sale);
                        ps.setBoolean(13, cs.isAdmin());
                        ps.execute();
                        ps.close();
                        ps0.close();
                        con.close();
                    }
                } else {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE magstar_shop_data SET uuid=?, item=?, nbt=?, id=?, price=?, tax=?, store=?, sale=?, admin=? WHERE (x=?) AND (y=?) AND (z=?) AND (world=?)"
                    );
                    ps.setString(1, cs.getOwner().toString());
                    ItemStack item = cs.getItem();
                    item.setItemMeta(cs.getMeta());
                    byte[] data = item.serializeAsBytes();
                    Blob blob = con.createBlob();
                    blob.setBytes(1, data);
                    ps.setBlob(2, blob);
                    ps.setString(3, getNBTString(cs.getItem()));
                    ps.setString(4, cs.getCurrency().getId());
                    ps.setInt(5, cs.getPrice());
                    ps.setDouble(6, cs.getTax());
                    ps.setInt(7, cs.getStore());
                    ps.setBoolean(8, cs.getType() == ShopType.sale);
                    ps.setBoolean(9, cs.isAdmin());
                    ps.setInt(10, cs.getLocation().getBlockX());
                    ps.setInt(11, cs.getLocation().getBlockY());
                    ps.setInt(12, cs.getLocation().getBlockZ());
                    ps.setString(13, cs.getLocation().getWorld().getName());
                    ps.execute();
                    ps.close();
                    ps0.close();
                    con.close();
                }
            } catch (SQLException e) {
                putError(e);
            }
            cs.removeItem();
            cs.setHasRemoved(true);
        }
    }
    @Override
    public void deleteData(ChestShop cs) {
        try {
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps0 = con.prepareStatement(
                    "DELETE FROM magstar_shop_data WHERE (x=?) AND (y=?) AND (z=?) AND (world LIKE ?)"
            );
            ps0.setInt(1, cs.getLocation().getBlockX());
            ps0.setInt(2, cs.getLocation().getBlockY());
            ps0.setInt(3, cs.getLocation().getBlockZ());
            ps0.setString(4, cs.getLocation().getWorld().getName());
            ps0.execute();
            ps0.close();
            con.close();
            selfUpdate();
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void loadItems() {
        try {
            List<String> list = new ArrayList<>();
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT uuid FROM magstar_item_data"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                if (!list.contains(uuid)) {
                    list.add(uuid);
                    Map<Integer, ItemStack> itemList = new HashMap<>();
                    PreparedStatement ps1 = conn.prepareStatement(
                            "SELECT id, item, amount FROM magstar_item_data WHERE uuid=?"
                    );
                    ps1.setString(1, uuid);
                    ResultSet rs1 = ps1.executeQuery();
                    while(rs1.next()) {
                        int id = rs1.getInt(1);
                        ItemStack item = ItemStack.deserializeBytes(GeneralUtils.blobToBytes(rs1.getBlob(2)));
                        int amount = rs1.getInt(3);
                        item.setAmount(amount);
                        if (item.getType() != Material.AIR) {
                            itemList.put(id, item);
                        }
                    }
                    ps1.close();
                    RuntimeDataManager.loadItem(uuid, itemList);
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void saveItems() {
        Map<String, Map<Integer, ItemStack>> map = RuntimeDataManager.getItemMap();
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            for (String p : map.keySet()) {
                for (Map.Entry<Integer, ItemStack> entry : map.get(p).entrySet()) {
                    int id = entry.getKey();
                    PreparedStatement ps0 = conn.prepareStatement(
                            "SELECT item, amount FROM magstar_item_data WHERE (uuid=?) AND (id=?)"
                    );
                    ps0.setString(1, p);
                    ps0.setInt(2, id);
                    ResultSet rs = ps0.executeQuery();
                    if (rs.next()) {
                        PreparedStatement ps1 = conn.prepareStatement(
                                "UPDATE magstar_item_data SET item=?, amount=? WHERE (uuid=?) AND (id=?)"
                        );
                        Blob blob = conn.createBlob();
                        blob.setBytes(1, entry.getValue().serializeAsBytes());
                        ps1.setBlob(1, blob);
                        ps1.setInt(2, entry.getValue().getAmount());
                        ps1.setString(3, p);
                        ps1.setInt(4, id);
                        ps1.execute();
                        ps1.close();
                    } else {
                        PreparedStatement ps1 = conn.prepareStatement(
                                "INSERT INTO magstar_item_data " +
                                        "(uuid, id, item, amount)" +
                                        "VALUES (?, ?, ?, ?)"
                        );
                        ps1.setString(1, p);
                        ps1.setInt(2, entry.getKey());
                        Blob blob = conn.createBlob();
                        blob.setBytes(1, entry.getValue().serializeAsBytes());
                        ps1.setBlob(3, blob);
                        ps1.setInt(4, entry.getValue().getAmount());
                        ps1.execute();
                        ps1.close();
                    }
                }
            }
            //selfUpdate();
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void deleteItems(Player p, int id) {
        try {
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM magstar_item_data WHERE (uuid=?) AND (id=?)"
            );
            ps.setString(1, p.getUniqueId().toString());
            ps.setInt(2, id);
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            putError(e);
        }
    }

    public String getNBTString(ItemStack i) {
        NBTItem item = new NBTItem(i);
        StringBuilder s = new StringBuilder();
        for (String key : item.getKeys()) {
            s.append(key).append("$").append(item.getString(key)).append("@");
        }
        return s.toString();
    }
    public Map<String, String> executeNBTString(String s) {
        String[] entries = s.split("@");
        Map<String, String> map = new HashMap<>();
        for (String entry : entries) {
            String[] val = entry.split("\\$");
            if(val.length == 1) {
                map.put(val[0], "");
            } else {
                map.put(val[0], val[1]);
            }
        }
        return map;
    }

    public void putError(Exception e) {
        Shop.getInstance().getLogger().info("数据库操作失败。将采用备用数据存储方法。");
        Shop.dbAvailable = false;
        e.printStackTrace();
    }
    public void selfUpdate() {
        try {
            List<UUID> list = new ArrayList<>();
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement(
                    "SELECT uuid FROM magstar_item_data"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!list.contains(UUID.fromString(rs.getString(1)))) {
                    list.add(UUID.fromString(rs.getString(1)));
                    int id = 1;
                    PreparedStatement ps1 = con.prepareStatement(
                            "SELECT id FROM magstar_item_data WHERE uuid=?"
                    );
                    ps1.setString(1, rs.getString(1));
                    ResultSet rs1 = ps1.executeQuery();
                    while (rs1.next()) {
                        int originId = rs1.getInt(1);
                        PreparedStatement ps2 = con.prepareStatement(
                                "UPDATE magstar_item_data SET id=? WHERE (uuid=?) AND (id=?)"
                        );
                        ps2.setInt(1, id);
                        ps2.setString(2, rs.getString(1));
                        ps2.setInt(3, originId);
                        ps2.execute();
                        ps2.close();
                        id++;
                    }
                    ps1.close();
                }
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            putError(e);
        }
    }
}
