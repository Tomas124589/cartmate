package cz.orv0005.cartmate.models;

import java.time.LocalDate;

import cz.orv0005.cartmate.SQLiteHelper;

public class ShoppingList {
    private long id = 0;
    private final String name;
    private final String shopName;
    private LocalDate date;

    public ShoppingList(String name, String shopName, String date) {
        this(name, shopName, (LocalDate) null);
        this.date = SQLiteHelper.str2LocalDate(date);
    }

    public ShoppingList(String name, String shopName, LocalDate date) {
        this.name = name;
        this.shopName = shopName;
        this.date = date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShopName() {
        return shopName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getTotalItemsCount() {
        return 0;
    }
}
