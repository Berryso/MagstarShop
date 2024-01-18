package top.magstar.shop.utils.fileutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.shop.Shop;

import java.io.File;
import java.util.Objects;

public enum Message {
    noPerm {
        @Override
        public String toString() {
            return "no-permission";
        }
    },
    quit,
    create,
    price,
    success,
    takeOut {
        @Override
        public String toString() {
            return "take_out";
        }
    },
    notEnough {
        @Override
        public String toString() {
            return "not_enough";
        }
    },
    notEmpty {
        @Override
        public String toString() {
            return "not_empty";
        }
    },
    getItem {
        @Override
        public String toString() {
            return "get_item";
        }
    },
    removeConfirm {
        @Override
        public String toString() {
            return "remove_confirm";
        }
    },
    removeAccepted {
        @Override
        public String toString() {
            return "remove_accepted";
        }
    },
    removeCancelled {
        @Override
        public String toString() {
            return "remove_cancelled";
        }
    },
    purchaseInput {
        @Override
        public String toString() {
            return "purchase_input";
        }
    },
    noMoney {
        @Override
        public String toString() {
            return "no_money";
        }
    },
    purchaseConfirm {
        @Override
        public String toString() {
            return "purchase_confirm";
        }
    },
    sellInput {
        @Override
        public String toString() {
            return "sell_input";
        }
    },
    sellConfirm {
        @Override
        public String toString() {
            return "sell_confirm";
        }
    },
    noVolume {
        @Override
        public String toString() {
            return "no_volume";
        }
    },
    purchaseOverFlow {
        @Override
        public String toString() {
            return "purchase_overflow";
        }
    },
    wrongSyntax {
        @Override
        public String toString() {
            return "wrong_syntax";
        }
    },
    onlyPlayer {
        @Override
        public String toString() {
            return "only_player";
        }
    },
    reloadSuccess {
        @Override
        public String toString() {
            return "reload_success";
        }
    },
    notShop {
        @Override
        public String toString() {
            return "not_shop";
        }
    },
    setAdmin {
        @Override
        public String toString() {
            return "set_admin";
        }
    },
    setPlayer {
        @Override
        public String toString() {
            return "set_player";
        }
    },
    onEdit {
        @Override
        public String toString() {
            return "on_edit";
        }
    },
    onTrade {
        @Override
        public String toString() {
            return "on_trade";
        }
    },
    onCreate {
        @Override
        public String toString() {
            return "on_create";
        }
    },
    otherAccessing {
        @Override
        public String toString() {
            return "other_accessing";
        }
    },
    ownerEditing {
        @Override
        public String toString() {
            return "owner_editing";
        }
    };
    public String getTranslate() {
        return Objects.requireNonNull(getConfig().getString(this.toString())).replace("&", "§");
    }
    public static File f = new File(Shop.getInstance().getDataFolder() + "/message.yml");
    public static void reload() {
        if (!f.exists()) {
            Shop.getInstance().saveResource("message.yml", false);
        }
        f = new File(Shop.getInstance().getDataFolder() + "/message.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
}
