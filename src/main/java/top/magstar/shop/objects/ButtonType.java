package top.magstar.shop.objects;

import java.util.Arrays;

public enum ButtonType {
    location,
    price,
    player,
    item,
    tax,
    remove,
    nextPage{
        @Override
        public String toString() {
            return "next_page";
        }
    },
    prevPage{
        @Override
        public String toString() {
            return "prev_page";
        }
    },
    side,
    trade;
    public static ButtonType getType(String s) {
        for (ButtonType type : Arrays.asList(ButtonType.location, ButtonType.price, ButtonType.player, ButtonType.item, ButtonType.tax, ButtonType.remove, ButtonType.nextPage, ButtonType.prevPage, ButtonType.side)) {
            if (type.toString().equals(s)) {
                return type;
            }
        }
        return null;
    }
}
