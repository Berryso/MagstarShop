package top.magstar.shop.objects;

import org.bukkit.scheduler.BukkitRunnable;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.statics.AbstractDataManager;
import top.magstar.shop.datamanagers.statics.IDataManager;
import top.magstar.shop.utils.fileutils.ConfigUtils;

public class Schedule {
    public static void saveData() {
        int cycle = ConfigUtils.getConfig().getInt("save-cycle");
        new BukkitRunnable() {
            @Override
            public void run() {
                IDataManager idm = AbstractDataManager.getInstance();
                idm.saveData();
                idm.saveItems();
                idm.loadData();
                idm.loadItems();
                Shop.getInstance().getLogger().info("已自动存储数据。");
            }
        }.runTaskTimer(Shop.getInstance(), cycle * 1200L, cycle * 1200L);
    }
}
