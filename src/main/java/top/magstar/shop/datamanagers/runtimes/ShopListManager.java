package top.magstar.shop.datamanagers.runtimes;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopListManager {
    private static final Map<Player, Integer> map = new HashMap<>();
    public synchronized static Map<Player, Integer> getMap() {
        return map;
    }
    public synchronized static void addPlayer(Player p) {
        map.put(p, 1);
    }
    public synchronized static void addPage(Player p) {
        map.put(p, map.get(p) + 1);
    }
    public synchronized static void minusPage(Player p) {
        map.put(p, map.get(p) - 1);
    }
    public synchronized static void removePlayer(Player p) {
        map.remove(p);
    }
    public synchronized static boolean isContain(Player p) {
        return map.containsKey(p);
    }
    public synchronized static void removeAll() {
        map.clear();
    }
    public synchronized static int getPage(Player p) {
        return map.get(p);
    }
}
