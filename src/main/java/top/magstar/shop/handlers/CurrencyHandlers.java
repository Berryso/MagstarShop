package top.magstar.shop.handlers;

import top.magstar.economy.datamanagers.AbstractDataManager;
import top.magstar.economy.objects.Currency;
import top.magstar.economy.objects.CurrencyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CurrencyHandlers {
    public static List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        Set<String> currencyIDs = CurrencyManager.getCurrencies();
        for (String id : currencyIDs) {
            currencies.add(CurrencyManager.byName(id));
        }
        return currencies;
    }
    public static Currency currencyForName(String name) {
        for (Currency c : getAllCurrencies()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    public static void addCurrency(UUID uuid, Currency currency, int value) {
        AbstractDataManager.getDataManagerInstance().setPlayerCoin(uuid, currency.getId(), AbstractDataManager.getDataManagerInstance().getPlayerCoin(uuid, currency.getId()) + value);
    }
    public static void takeCurrency(UUID uuid, Currency c, int value) {
        int origin = AbstractDataManager.getDataManagerInstance().getPlayerCoin(uuid, c.getId());
        if (origin > value) {
            AbstractDataManager.getDataManagerInstance().setPlayerCoin(uuid, c.getId(), origin - value);
        } else {
            AbstractDataManager.getDataManagerInstance().setPlayerCoin(uuid, c.getId(), 0);
        }
    }
    public static int getCurrency(UUID uuid, Currency c) {
        return AbstractDataManager.getDataManagerInstance().getPlayerCoin(uuid, c.getId());
    }
}
