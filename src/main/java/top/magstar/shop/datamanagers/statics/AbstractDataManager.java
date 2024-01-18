package top.magstar.shop.datamanagers.statics;

import top.magstar.shop.Shop;

public abstract class AbstractDataManager implements IDataManager {
    public static IDataManager getInstance() {
        if (Shop.dbAvailable) {
            return new SQLManager();
        } else {
            return new FileManager();
        }
    }
}
