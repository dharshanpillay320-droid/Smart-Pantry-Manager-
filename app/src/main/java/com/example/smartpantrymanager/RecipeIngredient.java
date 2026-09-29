package com.example.smartpantrymanager;

public class RecipeIngredient {
    private String name;
    private double quantity;
    private String unit;

    // Initializes a new recipe ingredient requirement
    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // getters for requirement properties
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
