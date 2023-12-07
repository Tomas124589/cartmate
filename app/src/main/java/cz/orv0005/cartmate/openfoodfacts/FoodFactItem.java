package cz.orv0005.cartmate.openfoodfacts;

public class FoodFactItem {

    private final String name;
    private final String category;

    public FoodFactItem(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

}
