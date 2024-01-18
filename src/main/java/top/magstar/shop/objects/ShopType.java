package top.magstar.shop.objects;

public enum ShopType {
    sale,
    purchase;
    public static ShopType getType(String s) {
        if (s.equals("sale")) {
            return sale;
        }
        else if (s.equals("purchase")) {
            return purchase;
        }
        else {
            return null;
        }
    }
    public ShopType getOppositeType() {
        if (this == sale) {
            return purchase;
        }
        if (this == purchase) {
            return sale;
        }
        return null;
    }
}
