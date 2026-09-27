package com.example.smartpantrymanager;

import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private String description;
    private List<RecipeIngredient> requiredIngredients;
    private String instructions;

    public Recipe(String id, String title, String description, List<RecipeIngredient> requiredIngredients, String instructions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredIngredients = requiredIngredients;
        this.instructions = instructions;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<RecipeIngredient> getRequiredIngredients() { return requiredIngredients; }
    public String getInstructions() { return instructions; }
}
