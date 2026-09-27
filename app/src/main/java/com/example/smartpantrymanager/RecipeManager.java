package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {
    private static RecipeManager instance;
    private List<Recipe> allRecipes;

    private RecipeManager() {
        allRecipes = new ArrayList<>();
        seedTestRecipes();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    private void seedTestRecipes() {
        List<RecipeIngredient> soupIngredients = new ArrayList<>();
        soupIngredients.add(new RecipeIngredient("Tomato", 500, "g"));
        soupIngredients.add(new RecipeIngredient("Onion", 1, "pieces"));
        allRecipes.add(new Recipe("1", "Tomato Soup", "A warm and hearty tomato soup.", soupIngredients, "1. Chop tomatoes and onions.\n2. Boil until soft.\n3. Blend until smooth."));

        List<RecipeIngredient> pastaIngredients = new ArrayList<>();
        pastaIngredients.add(new RecipeIngredient("Pasta", 250, "g"));
        pastaIngredients.add(new RecipeIngredient("Tomato", 200, "g"));
        allRecipes.add(new Recipe("2", "Simple Pasta", "Quick tomato pasta.", pastaIngredients, "1. Boil pasta.\n2. Make tomato sauce.\n3. Mix and serve."));

        List<RecipeIngredient> omeletteIngredients = new ArrayList<>();
        omeletteIngredients.add(new RecipeIngredient("Egg", 3, "pieces"));
        omeletteIngredients.add(new RecipeIngredient("Milk", 50, "ml"));
        allRecipes.add(new Recipe("3", "Fluffy Omelette", "A simple and quick breakfast.", omeletteIngredients, "1. Whisk eggs and milk.\n2. Fry in a pan until golden."));
    }

    public List<Recipe> getMatchedRecipes(List<Ingredient> pantry) {
        List<Recipe> matched = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantry)) {
                matched.add(recipe);
            }
        }
        return matched;
    }

    public Recipe getRecipeById(String id) {
        for (Recipe r : allRecipes) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    private boolean canMakeRecipe(Recipe recipe, List<Ingredient> pantry) {
        for (RecipeIngredient required : recipe.getRequiredIngredients()) {
            boolean found = false;
            String normalizedRequiredName = IngredientManager.getInstance().normalizeName(required.getName());

            for (Ingredient pantryItem : pantry) {
                if (pantryItem.getNormalizedName().equals(normalizedRequiredName)) {
                    if (hasSufficientQuantity(required, pantryItem)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                return false; 
            }
        }
        return true; 
    }

    private boolean hasSufficientQuantity(RecipeIngredient required, Ingredient pantryItem) {
        String reqUnit = required.getUnit();
        double reqQty = required.getQuantity();
        String panUnit = pantryItem.getUnit();
        double panQty = pantryItem.getQuantity();

        if (reqUnit.equals(panUnit)) {
            return panQty >= reqQty;
        }

        if (reqUnit.equals("g") && panUnit.equals("kg")) {
            return panQty * 1000.0 >= reqQty;
        }
        if (reqUnit.equals("kg") && panUnit.equals("g")) {
            return panQty / 1000.0 >= reqQty;
        }
        if (reqUnit.equals("ml") && panUnit.equals("L")) {
            return panQty * 1000.0 >= reqQty;
        }
        if (reqUnit.equals("L") && panUnit.equals("ml")) {
            return panQty / 1000.0 >= reqQty;
        }

        return false;
    }
}
