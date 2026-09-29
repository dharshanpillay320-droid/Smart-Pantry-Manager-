package com.example.smartpantrymanager;

public class Ingredient {
    private String id;
    private String originalName;
    private String normalizedName;
    private double quantity;
    private String unit;
    private String expiryDate;

    // Initializes a new pantry ingredient record
    public Ingredient(String id, String originalName, String normalizedName, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.originalName = originalName;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // getters and setters for ingredient properties
    public String getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getNormalizedName() { return normalizedName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
