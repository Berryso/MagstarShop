package top.magstar.shop.datamanagers.statics;

import org.bukkit.entity.Player;
import top.magstar.shop.objects.ChestShop;

public interface IDataManager {
    void reload();
    void loadData();
    void saveData();
    void deleteData(ChestShop cs);
    void loadItems();
    void saveItems();
    void deleteItems(Player p, int id);
}
