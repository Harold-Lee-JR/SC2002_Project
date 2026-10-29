package entity;

/** One dish on the menu: name, base price and the number of preparation units the Chef must spend on it. */
public class MenuItem {
    private final String name;
    private final double basePrice;
    private final int preparationUnits;

    public MenuItem(String name, double basePrice, int preparationUnits) {
        this.name = name;
        this.basePrice = basePrice;
        this.preparationUnits = preparationUnits;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return basePrice;
    }

    public int getPrepUnit() {
        return preparationUnits;
    }
}
