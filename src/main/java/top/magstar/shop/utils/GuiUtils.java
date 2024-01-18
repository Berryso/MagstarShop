package top.magstar.shop.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import top.magstar.shop.datamanagers.runtimes.RuntimeDataManager;
import top.magstar.shop.objects.ButtonType;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ShopType;
import top.magstar.shop.utils.fileutils.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public final class GuiUtils {
    public static List<Integer> getShopSideLocations() {
        FileConfiguration cfg = ShopInfo.getConfig();
        List<Integer> sideList = new ArrayList<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            String type = cfg.getString("Buttons." + s + ".Type");
            if (Objects.equals(type, "side")) {
                sideList.addAll(cfg.getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideList;
    }
    public static List<Integer> getViewerSideLocations() {
        FileConfiguration cfg = Viewer.getConfig();
        List<Integer> sideList = new ArrayList<>();
        for (String s : cfg.getConfigurationSection("Buttons").getKeys(false)) {
            String type = cfg.getString("Buttons." + s + ".Type");
            if (Objects.equals(type, "side")) {
                sideList.addAll(cfg.getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideList;
    }
    public static Map<ButtonType, Integer> getOtherLocations() {
        FileConfiguration cfg = ShopInfo.getConfig();
        Map<ButtonType, Integer> map = new HashMap<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            String type = cfg.getString("Buttons." + s + ".Type");
            if (ButtonType.getType(type) != null) {
                map.put(ButtonType.getType(type), cfg.getInt("Buttons." + s + ".Location"));
            }
        }
        return map;
    }
    public static Map<ItemStack, List<Integer>> getSideList(ChestShop cs) {
        FileConfiguration cfg = ShopInfo.getConfig();
        Map<ItemStack, List<Integer>> sideList = new HashMap<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            Material mat = Material.getMaterial(s.toUpperCase());
            if (mat == null && !s.equalsIgnoreCase("<item>")) {
                throw new IllegalArgumentException("Material not found: " + s);
            }
            ItemStack i = new ItemStack(mat == null ? cs.getItem().getType() : mat, 1);
            ItemMeta meta = i.getItemMeta();
            if (Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type")).equalsIgnoreCase("side")) {
                String display = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                List<String> lore = new ArrayList<>();
                List<String> originLore = cfg.getStringList("Buttons." + s + ".Lore");
                for (String lores : originLore) {
                    lore.add(GeneralUtils.getTranslate(lores));
                }
                meta.setDisplayName(display);
                meta.setLore(lore);
                i.setItemMeta(meta);
                sideList.put(i, cfg.getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideList;
    }
    public static Map<ItemStack, List<Integer>> getTradeSideList(ChestShop cs) {
        FileConfiguration cfg = Trade.getConfig();
        Map<ItemStack, List<Integer>> sideList = new HashMap<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            Material mat = Material.getMaterial(s.toUpperCase());
            if (mat == null && !s.equalsIgnoreCase("<item>")) {
                throw new IllegalArgumentException("Material not found: " + s);
            }
            ItemStack i = new ItemStack(mat == null ? cs.getItem().getType() : mat, 1);
            ItemMeta meta = i.getItemMeta();
            if (Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type")).equalsIgnoreCase("side")) {
                String display = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                List<String> lore = new ArrayList<>();
                List<String> originLore = cfg.getStringList("Buttons." + s + ".Lore");
                for (String lores : originLore) {
                    lore.add(GeneralUtils.getTranslate(lores));
                }
                meta.setDisplayName(display);
                meta.setLore(lore);
                i.setItemMeta(meta);
                sideList.put(i, cfg.getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideList;
    }
    public static Map<ButtonType, Map.Entry<ItemStack, Integer>> getOtherList(ChestShop cs) {
        FileConfiguration cfg = ShopInfo.getConfig();
        Map<ButtonType, Map.Entry<ItemStack, Integer>> otherList = new HashMap<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            Material mat = Material.getMaterial(s.toUpperCase());
            if (mat == null && !s.equalsIgnoreCase("<item>")) {
                throw new IllegalArgumentException("Material not found: " + s);
            }
            NBTItem nbtItem = new NBTItem(new ItemStack(mat == null ? cs.getItem().getType() : mat, 1));
            nbtItem.applyNBT(cs.getItem());
            ItemStack i = nbtItem.getItem();
            ItemMeta meta = i.getItemMeta();
            if (!Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type")).equalsIgnoreCase("side")) {
                switch (Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type"))) {
                    case "location" -> {
                        String display0 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores0 = new ArrayList<>();
                        List<String> originLore0 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore0) {
                            lores0.add(GeneralUtils.getTranslate(lore)
                                    .replace("{world}", cs.getLocation().getWorld().getName())
                                    .replace("{location_x}", String.valueOf(cs.getLocation().getBlockX()))
                                    .replace("{location_y}", String.valueOf(cs.getLocation().getBlockY()))
                                    .replace("{location_z}", String.valueOf(cs.getLocation().getBlockZ()))
                            );
                        }
                        meta.setDisplayName(display0);
                        meta.setLore(lores0);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.location, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "price" -> {
                        String display1 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores1 = new ArrayList<>();
                        List<String> originLore1 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore1) {
                            lores1.add(GeneralUtils.getTranslate(lore)
                                    .replace("{price}", String.valueOf(cs.getPrice()))
                                    .replace("{currency}", cs.getCurrency().getName())
                            );
                        }
                        meta.setDisplayName(display1);
                        meta.setLore(lores1);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.price, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "player" -> {
                        String display2 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display"))).replace("{player}", Objects.requireNonNull(Bukkit.getPlayer(cs.getOwner())).getName());
                        List<String> lores2 = new ArrayList<>();
                        List<String> originLore2 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore2) {
                            lore = lore.replace("{player}", Objects.requireNonNull(Bukkit.getPlayer(cs.getOwner())).getName()
                            );
                            Pattern pattern = Pattern.compile("\\{[^{}]*}");
                            Matcher matcher = pattern.matcher(lore);
                            if (matcher.find()) {
                                String regex = matcher.group();
                                String origin = regex.replace("{", "").replace("}", "").replace("<", "").replace(">", "").split("\\?")[1];
                                if (lore.contains(":")) {
                                    if (cs.getType() == ShopType.sale) {
                                        lore = lore.replace(regex, origin.split(":")[0]);
                                    } else {
                                        lore = lore.replace(regex, origin.split(":")[1]);
                                    }
                                }
                            }
                            lore = GeneralUtils.getTranslate(lore);
                            lores2.add(lore);
                        }
                        meta.setDisplayName(display2);
                        meta.setLore(lores2);
                        SkullMeta sm = (SkullMeta) meta;
                        String[] args = Objects.requireNonNull(cfg.getString("Buttons." + s + ".Skin")).split(":");
                        if (args[0].equalsIgnoreCase("player")) {
                            sm.setOwningPlayer(Bukkit.getOfflinePlayer(args[1].equals("{player}") ? Objects.requireNonNull(Bukkit.getPlayer(cs.getOwner())).getName() : args[1]));
                        } else if (args[0].equalsIgnoreCase("uuid")) {
                            sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(args[1])));
                        }
                        i.setItemMeta(sm);
                        otherList.put(ButtonType.player, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "item" -> {
                        String display3 = GeneralUtils.getTranslate(cs.getMeta().hasDisplayName() ? Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")).replace("{item_display}", cs.getMeta().getDisplayName()) : cs.getItem().getType().toString());
                        List<String> lores3 = new ArrayList<>();
                        List<String> itemLore = cs.getMeta().getLore();
                        List<String> originLore3 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore3) {
                            if (lore.equals("{item_lore}")) {
                                if (!(itemLore == null)) {
                                    lores3.addAll(itemLore);
                                }
                            } else {
                                lores3.add(GeneralUtils.getTranslate(lore)
                                        .replace("{store}", cs.isAdmin() ? "∞" : String.valueOf(cs.getStore()))
                                );
                            }
                        }
                        meta.setDisplayName(display3);
                        meta.setLore(lores3);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.item, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "tax" -> {
                        String display4 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores4 = new ArrayList<>();
                        List<String> originLore4 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore4) {
                            lores4.add(GeneralUtils.getTranslate(lore)
                                    .replace("{tax}", String.valueOf(cs.getTax() * cs.getPrice()))
                            );
                        }
                        meta.setDisplayName(display4);
                        meta.setLore(lores4);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.tax, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "remove" -> {
                        String display5 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores5 = new ArrayList<>();
                        List<String> originLore5 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore5) {
                            lores5.add(GeneralUtils.getTranslate(lore));
                        }
                        meta.setDisplayName(display5);
                        meta.setLore(lores5);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.remove, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                }
            }
        }
        return otherList;
    }
    public static Map<ButtonType, Map.Entry<ItemStack, Integer>> getTradeOtherList(ChestShop cs, Player p) {
        FileConfiguration cfg = Trade.getConfig();
        Map<ButtonType, Map.Entry<ItemStack, Integer>> otherList = new HashMap<>();
        for (String s : Objects.requireNonNull(cfg.getConfigurationSection("Buttons")).getKeys(false)) {
            Material mat = Material.getMaterial(s.toUpperCase());
            if (mat == null && !s.equalsIgnoreCase("<item>")) {
                throw new IllegalArgumentException("Material not found: " + s);
            }
            ItemStack i = new ItemStack(mat == null ? cs.getItem().getType() : mat, 1);
            ItemMeta meta = i.getItemMeta();
            if (!Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type")).equalsIgnoreCase("side")) {
                switch (Objects.requireNonNull(cfg.getString("Buttons." + s + ".Type"))) {
                    case "location" -> {
                        String display0 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores0 = new ArrayList<>();
                        List<String> originLore0 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore0) {
                            lores0.add(GeneralUtils.getTranslate(lore)
                                    .replace("{world}", cs.getLocation().getWorld().getName())
                                    .replace("{location_x}", String.valueOf(cs.getLocation().getBlockX()))
                                    .replace("{location_y}", String.valueOf(cs.getLocation().getBlockY()))
                                    .replace("{location_z}", String.valueOf(cs.getLocation().getBlockZ()))
                            );
                        }
                        meta.setDisplayName(display0);
                        meta.setLore(lores0);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.location, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "price" -> {
                        String display1 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores1 = new ArrayList<>();
                        List<String> originLore1 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore1) {
                            lores1.add(GeneralUtils.getTranslate(lore)
                                    .replace("{price}", String.valueOf(cs.getPrice()))
                                    .replace("{currency}", cs.getCurrency().getName())
                            );
                        }
                        meta.setDisplayName(display1);
                        meta.setLore(lores1);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.price, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "player" -> {
                        String display2 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display"))).replace("{player}", p.getName()).replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(cs.getOwner()).getName()));
                        List<String> lores2 = new ArrayList<>();
                        List<String> originLore2 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore2) {
                            lore = lore.replace("{player}", p.getName()).replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(cs.getOwner()).getName()));
                            Pattern pattern = Pattern.compile("\\{[^{}]*}");
                            Matcher matcher = pattern.matcher(lore);
                            if (matcher.find()) {
                                String regex = matcher.group();
                                String origin = regex.replace("{", "").replace("}", "").replace("<", "").replace(">", "").split("\\?")[1];
                                if (lore.contains(":")) {
                                    if (cs.getType() == ShopType.sale) {
                                        lore = lore.replace(regex, origin.split(":")[0]);
                                    } else {
                                        lore = lore.replace(regex, origin.split(":")[1]);
                                    }
                                }
                            }
                            lore = GeneralUtils.getTranslate(lore);
                            lores2.add(lore);
                        }
                        meta.setDisplayName(display2);
                        meta.setLore(lores2);
                        SkullMeta sm = (SkullMeta) meta;
                        String[] args = Objects.requireNonNull(cfg.getString("Buttons." + s + ".Skin")).split(":");
                        if (args[0].equalsIgnoreCase("player")) {
                            if (args[1].equalsIgnoreCase("{player}")) {
                                sm.setOwningPlayer(p);
                            } else if (args[1].equalsIgnoreCase("{owner}")) {
                                sm.setOwningPlayer(Bukkit.getOfflinePlayer(cs.getOwner()));
                            } else {
                                sm.setOwningPlayer(Bukkit.getOfflinePlayer(args[1]));
                            }
                        } else if (args[0].equalsIgnoreCase("uuid")) {
                            sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(args[1])));
                        }
                        i.setItemMeta(sm);
                        otherList.put(ButtonType.player, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "item" -> {
                        String display3 = GeneralUtils.getTranslate(cs.getMeta().hasDisplayName() ? Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")).replace("{item_display}", cs.getMeta().getDisplayName()) : cs.getItem().getType().toString());
                        List<String> lores3 = new ArrayList<>();
                        List<String> itemLore = cs.getMeta().getLore();
                        List<String> originLore3 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore3) {
                            if (lore.equals("{item_lore}")) {
                                if (!(itemLore == null)) {
                                    lores3.addAll(itemLore);
                                }
                            } else {
                                lores3.add(GeneralUtils.getTranslate(lore)
                                        .replace("{store}", String.valueOf(cs.getStore()))
                                );
                            }
                        }
                        meta.setDisplayName(display3);
                        meta.setLore(lores3);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.item, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "tax" -> {
                        String display4 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores4 = new ArrayList<>();
                        List<String> originLore4 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore4) {
                            lores4.add(GeneralUtils.getTranslate(lore)
                                    .replace("{tax}", String.valueOf(cs.getTax() * cs.getPrice()))
                            );
                        }
                        meta.setDisplayName(display4);
                        meta.setLore(lores4);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.tax, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                    case "trade" -> {
                        String display5 = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Buttons." + s + ".Display")));
                        List<String> lores5 = new ArrayList<>();
                        List<String> originLore5 = cfg.getStringList("Buttons." + s + ".Lore");
                        for (String lore : originLore5) {
                            lores5.add(GeneralUtils.getTranslate(lore));
                        }
                        meta.setDisplayName(display5);
                        meta.setLore(lores5);
                        i.setItemMeta(meta);
                        otherList.put(ButtonType.remove, new Map.Entry<>() {
                            @Override
                            public ItemStack getKey() {
                                return i;
                            }

                            @Override
                            public Integer getValue() {
                                return cfg.getInt("Buttons." + s + ".Location");
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                return null;
                            }
                        });
                    }
                }
            }
        }
        return otherList;
    }
    public static void createShopInfoGui(ChestShop cs, Player p) {
        FileConfiguration cfg = ShopInfo.getConfig();
        Map<ItemStack, List<Integer>> sideList = getSideList(cs);
        Map<ButtonType, Map.Entry<ItemStack, Integer>> otherList = getOtherList(cs);
        String title = GeneralUtils.getTranslate(Objects.requireNonNull(cfg.getString("Title")).replace("{player}", p.getName()));
        final int size = 27;
        Inventory inv = Bukkit.createInventory(p, size, title);
        for (ItemStack i : sideList.keySet()) {
            List<Integer> locList = sideList.get(i);
            for (int loc : locList) {
                inv.setItem(loc, i);
            }
        }
        for (Map.Entry<ItemStack, Integer> entry : otherList.values()) {
            inv.setItem(entry.getValue(), entry.getKey());
        }
        p.openInventory(inv);
    }
    public static Map<ItemStack, List<Integer>> getSideMap(ChestShop cs) {
        FileConfiguration cfg = Restocking.getConfig();
        Map<ItemStack, List<Integer>> sideMap = new HashMap<>();
        for (String s : cfg.getConfigurationSection("Buttons").getKeys(false)) {
            Material mat = Material.getMaterial(s.toUpperCase());
            if (mat != null) {
                String display = GeneralUtils.getTranslate(cfg.getString("Buttons." + s + ".Display"));
                List<String> lores = new ArrayList<>();
                List<String> originLore = cfg.getStringList("Buttons." + s + ".Lore");
                for (String lore : originLore) {
                    lores.add(GeneralUtils.getTranslate(lore));
                }
                ItemStack item = new ItemStack(mat, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(display);
                meta.setLore(lores);
                if (mat == Material.PLAYER_HEAD) {
                    String con = cfg.getString("Buttons." + s + ".Skin");
                    String[] args = con.split(":");
                    if (args[0].equalsIgnoreCase("player")) {
                        String playerName = args[1].replace("{player}", Bukkit.getPlayer(cs.getOwner()).getName());
                        ((SkullMeta) meta).setOwningPlayer(Bukkit.getPlayer(playerName));
                    }
                    if (args[0].equalsIgnoreCase("uuid")) {
                        ((SkullMeta) meta).setOwningPlayer(Bukkit.getPlayer(UUID.fromString(args[1])));
                    }
                    List<String> newLore = new ArrayList<>();
                    for (String lore : lores) {
                        if (lore.contains("{store}")) {
                            lore = lore.replace("{store}", String.valueOf(cs.getStore()));
                        }
                        newLore.add(lore);
                    }
                    meta.setLore(newLore);
                }
                item.setItemMeta(meta);
                sideMap.put(item, cfg.getIntegerList("Buttons." + s + ".Location"));
            }
            if (s.equals("<available_locations>")) {
                sideMap.put(null, cfg.getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideMap;
    }
    public static List<Integer> createRestockingGui(ChestShop cs, Player p) {
        Map<ItemStack, List<Integer>> sideMap = getSideMap(cs);
        List<Integer> availableLocations = new ArrayList<>();
        Inventory inv = Bukkit.createInventory(p, 54, GeneralUtils.getTranslate(Objects.requireNonNull(Restocking.getConfig().getString("Title"))).replace("{player}", p.getName()));
        for (ItemStack s : sideMap.keySet()) {
            if (s != null) {
                for (int i : sideMap.get(s)) {
                    inv.setItem(i, s);
                }
            }
            if (s == null) {
                availableLocations = sideMap.get(null);
            }
        }
        p.openInventory(inv);
        return availableLocations;
    }
    public static List<Integer> getAvailableLocations() {
        FileConfiguration cfg = Restocking.getConfig();
        return cfg.getIntegerList("Buttons.<available_locations>");
    }
    public static Map<ButtonType, Map<ItemStack, List<Integer>>> getViewerButtonMap(Player p, int thisPage, int allPage) {
        Map<ButtonType, Map<ItemStack, List<Integer>>> map = new HashMap<>();
        map.put(ButtonType.side, new HashMap<>());
        map.put(ButtonType.nextPage, new HashMap<>());
        map.put(ButtonType.prevPage, new HashMap<>());
        FileConfiguration cfg = Viewer.getConfig();
        for (String s : cfg.getConfigurationSection("Buttons").getKeys(false)) {
            if (!s.equals("<available_locations>")) {
                String path = "Buttons." + s + ".";
                ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(s.toUpperCase())), 1);
                String display = cfg.getString(path + "Display");
                ButtonType type = ButtonType.getType(cfg.getString(path + "Type"));
                List<String> originLore = cfg.getStringList(path + "Lore");
                List<String> lores = new ArrayList<>();
                List<Integer> locationList;
                switch (Objects.requireNonNull(type)) {
                    case side -> {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(GeneralUtils.getTranslate(display));
                        for (String lore : originLore) {
                            lores.add(GeneralUtils.getTranslate(lore));
                        }
                        locationList = cfg.getIntegerList(path + "Location");
                        meta.setLore(lores);
                        item.setItemMeta(meta);
                        map.get(ButtonType.side).put(item, locationList);
                    }
                    case prevPage -> {
                        if (thisPage != 1) {
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(GeneralUtils.getTranslate(display));
                            for (String lore : originLore) {
                                lores.add(GeneralUtils.getTranslate(lore).replace("{prev_page}", String.valueOf(thisPage - 1)));
                            }
                            locationList = Collections.singletonList(cfg.getInt(path + "Location"));
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            map.get(ButtonType.side).put(item, locationList);
                        }
                    }
                    case nextPage -> {
                        if (allPage != thisPage) {
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(GeneralUtils.getTranslate(display));
                            for (String lore : originLore) {
                                lores.add(GeneralUtils.getTranslate(lore).replace("{next_page}", String.valueOf(thisPage + 1)));
                            }
                            locationList = Collections.singletonList(cfg.getInt(path + "Location"));
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            map.get(ButtonType.side).put(item, locationList);
                        }
                    }
                }
            }
        }
        return map;
    }
    public static Map<ButtonType, Map<ItemStack, List<Integer>>> getShopListButtonMap(Player p, int thisPage, int allPage) {
        Map<ButtonType, Map<ItemStack, List<Integer>>> map = new HashMap<>();
        map.put(ButtonType.side, new HashMap<>());
        map.put(ButtonType.nextPage, new HashMap<>());
        map.put(ButtonType.prevPage, new HashMap<>());
        FileConfiguration cfg = ShopList.getConfig();
        for (String s : cfg.getConfigurationSection("Buttons").getKeys(false)) {
            if (!s.equals("<chest_shops>")) {
                String path = "Buttons." + s + ".";
                ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(s.toUpperCase())), 1);
                String display = cfg.getString(path + "Display");
                ButtonType type = ButtonType.getType(cfg.getString(path + "Type"));
                List<String> originLore = cfg.getStringList(path + "Lore");
                List<String> lores = new ArrayList<>();
                List<Integer> locationList;
                switch (Objects.requireNonNull(type)) {
                    case side -> {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(GeneralUtils.getTranslate(display));
                        for (String lore : originLore) {
                            lores.add(GeneralUtils.getTranslate(lore));
                        }
                        locationList = cfg.getIntegerList(path + "Location");
                        meta.setLore(lores);
                        item.setItemMeta(meta);
                        map.get(ButtonType.side).put(item, locationList);
                    }
                    case prevPage -> {
                        if (thisPage != 1) {
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(GeneralUtils.getTranslate(display));
                            for (String lore : originLore) {
                                lores.add(GeneralUtils.getTranslate(lore).replace("{prev_page}", String.valueOf(thisPage - 1)));
                            }
                            locationList = Collections.singletonList(cfg.getInt(path + "Location"));
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            map.get(ButtonType.side).put(item, locationList);
                        }
                    }
                    case nextPage -> {
                        if (allPage != thisPage) {
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(GeneralUtils.getTranslate(display));
                            for (String lore : originLore) {
                                lores.add(GeneralUtils.getTranslate(lore).replace("{next_page}", String.valueOf(thisPage + 1)));
                            }
                            locationList = Collections.singletonList(cfg.getInt(path + "Location"));
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            map.get(ButtonType.side).put(item, locationList);
                        }
                    }
                }
            }
        }
        return map;
    }
    public static void createShopListGui(Player p, int thisPage, int allPage) {
        Map<ButtonType, Map<ItemStack, List<Integer>>> buttonMap = getShopListButtonMap(p, thisPage, allPage);
        List<ChestShop> shops = RuntimeDataManager.getShops(p.getUniqueId());
        FileConfiguration cfg = ShopList.getConfig();
        Inventory inv = Bukkit.createInventory(p, 54, cfg.getString("Title").replace("{page}", String.valueOf(thisPage)));
        for (ButtonType type : buttonMap.keySet()) {
            Map<ItemStack, List<Integer>> map = buttonMap.get(type);
            for (Map.Entry<ItemStack, List<Integer>> entry : map.entrySet()) {
                for (int loc : entry.getValue()) {
                    inv.setItem(loc, entry.getKey());
                }
            }
        }
        List<Integer> locations = cfg.getIntegerList("Buttons.<chest_shops>.Location");
        String display = GeneralUtils.getTranslate(cfg.getString("Buttons.<chest_shops>.Display") + "");
        List<String> originLores = cfg.getStringList("Buttons.<chest_shops>.Lore");
        Material mat = Material.getMaterial(Objects.requireNonNull(cfg.getString("Buttons.<chest_shops>.Material")).toUpperCase());
        int index0 = 0;
        int index1 = 0;
        int size = locations.size();
        Comparator<Integer> c = (o1, o2) -> {
            if (o1 > o2) {
                return 1;
            } else if ((int)o1 == o2){
                return 0;
            } else {
                return -1;
            }
        };
        locations.sort(c);
        for (ChestShop cs : shops) {
            if (mat == null) {
                throw new IllegalArgumentException("No such material: " + Objects.requireNonNull(cfg.getString("Buttons.<chest_shops>.Material")).toUpperCase());
            }
            List<String> lores = new ArrayList<>();
            ItemStack item = new ItemStack(mat, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(display);
            for (String lore : originLores) {
                lore = lore.replace("{world}", cs.getLocation().getWorld().getName())
                        .replace("{location_x}", String.valueOf(cs.getLocation().getBlockX()))
                        .replace("{location_y}", String.valueOf(cs.getLocation().getBlockY()))
                        .replace("{location_z}", String.valueOf(cs.getLocation().getBlockZ()))
                        .replace("{item_display}", cs.getMeta().hasDisplayName() ? cs.getMeta().getDisplayName() : cs.getItem().getType().toString())
                        .replace("{price}", String.valueOf(cs.getPrice()))
                        .replace("{currency}", cs.getCurrency().getName())
                        .replace("{store}", cs.isAdmin() ? "∞" : String.valueOf(cs.getStore()))
                        .replace("{tax}", String.valueOf(cs.getTax()));
                Pattern pattern = Pattern.compile("\\{[^{}]*}");
                Matcher matcher = pattern.matcher(lore);
                if (matcher.find()) {
                    String regex = matcher.group();
                    String origin = regex.replace("{", "").replace("}", "").replace("<", "").replace(">", "").split("\\?")[1];
                    if (lore.contains(":")) {
                        if (cs.getType() == ShopType.sale) {
                            lore = lore.replace(regex, origin.split(":")[0]);
                        } else {
                            lore = lore.replace(regex, origin.split(":")[1]);
                        }
                    }
                }
                lore = GeneralUtils.getTranslate(lore);
                lores.add(lore);
            }
            meta.setLore(lores);
            item.setItemMeta(meta);
            if (index0 >= (thisPage - 1) * size && index0 < (thisPage * size - 1)) {
                inv.setItem(locations.get(index1), item);
                index1++;
            }
            index0++;
        }
        p.openInventory(inv);
    }
    public static void createViewerGui(Player p, int thisPage, int allPage) {
        Map<ButtonType, Map<ItemStack, List<Integer>>> buttonMap = getViewerButtonMap(p, thisPage, allPage);
        Map<Integer, ItemStack> itemMap = RuntimeDataManager.getItems(p.getUniqueId().toString());
        Inventory inv = Bukkit.createInventory(p, 54, Viewer.getConfig().getString("Title").replace("{page}", String.valueOf(thisPage)));
        for (ButtonType type : buttonMap.keySet()) {
            Map<ItemStack, List<Integer>> map = buttonMap.get(type);
            for (Map.Entry<ItemStack, List<Integer>> entry : map.entrySet()) {
                for (int loc : entry.getValue()) {
                    inv.setItem(loc, entry.getKey());
                }
            }
        }
        List<Integer> available = Viewer.getConfig().getIntegerList("Buttons.<available_locations>");
        if (itemMap != null) {
            Comparator<Integer> c = (o1, o2) -> {
                if (o1 > o2) {
                    return 1;
                } else if ((int)o1 == o2){
                    return 0;
                } else {
                    return -1;
                }
            };
            available.sort(c);
            int all = available.size();
            int block = 0;
            for (int i : itemMap.keySet()) {
                ItemStack item = new ItemStack(itemMap.get(i));
                int amount = item.getAmount();
                int max = item.getMaxStackSize();
                int sets = amount;
                int remain = 0;
                if (max != 1) {
                    sets = amount / max;
                    remain = amount % max;
                }
                for (int set = 0; set <= sets; set++) {
                    for (int loc : available) {
                        if (inv.getItem(loc) == null) {
                            block++;
                            if (block <= thisPage * all && block > (thisPage - 1) * all) {
                                if (set < sets) {
                                    item.setAmount(item.getMaxStackSize());
                                    inv.setItem(loc, item);
                                } else {
                                    if (remain > 0) {
                                        item.setAmount(remain);
                                        inv.setItem(loc, item);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        p.openInventory(inv);
    }
    public static void createTradeGui(ChestShop cs, Player p) {
        Map<ItemStack, List<Integer>> sideMap = getTradeSideList(cs);
        Map<ButtonType, Map.Entry<ItemStack, Integer>> otherMap = getTradeOtherList(cs, p);
        FileConfiguration cfg = Trade.getConfig();
        Inventory inv = Bukkit.createInventory(p, 27, Objects.requireNonNull(cfg.getString("Title")).replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(cs.getOwner()).getName())));
        for (ItemStack i : sideMap.keySet()) {
            for (int loc : sideMap.get(i)) {
                inv.setItem(loc, i);
            }
        }
        for (Map.Entry<ItemStack, Integer> entry : otherMap.values()) {
            inv.setItem(entry.getValue(), entry.getKey());
        }
        p.openInventory(inv);
    }
    public static int getTradeButtonLocation() {
        FileConfiguration cfg = Trade.getConfig();
        for (String s : cfg.getConfigurationSection("Buttons").getKeys(false)) {
            if (cfg.getString("Buttons." + s + ".Type").equalsIgnoreCase("trade")) {
                return cfg.getInt("Buttons." + s + ".Location");
            }
        }
        return -1;
    }
    public static int getPrevPageLocation() {
        for (String s : Viewer.getConfig().getConfigurationSection("Buttons").getKeys(false)) {
            if (Viewer.getConfig().getString("Buttons." + s + ".Type").equals("prev_page")) {
                return Viewer.getConfig().getInt("Buttons." + s + ".Location");
            }
        }
        return -1;
    }
    public static int getNextPageLocation() {
        for (String s : Viewer.getConfig().getConfigurationSection("Buttons").getKeys(false)) {
            if (Viewer.getConfig().getString("Buttons." + s + ".Type").equals("next_page")) {
                return Viewer.getConfig().getInt("Buttons." + s + ".Location");
            }
        }
        return -1;
    }
}
