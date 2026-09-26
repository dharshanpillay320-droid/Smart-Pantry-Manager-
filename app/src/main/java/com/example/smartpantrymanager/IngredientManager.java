package com.example.smartpantrymanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IngredientManager {

    private static IngredientManager instance;
    private List<Ingredient> pantryList;

    private IngredientManager() {
        pantryList = new ArrayList<>();
    }

    public static IngredientManager getInstance() {
        if (instance == null) {
            instance = new IngredientManager();
        }
        return instance;
    }

    public List<Ingredient> getPantryList() {
        return pantryList;
    }

    public void addOrUpdateIngredient(String name, double quantity, String unit, String expiryDate) {
        String normalized = normalizeName(name);

        for (Ingredient ingredient : pantryList) {
            if (ingredient.getNormalizedName().equals(normalized)) {
                aggregateQuantity(ingredient, quantity, unit);
                updateToEarliestExpiry(ingredient, expiryDate);
                normalizeUnits(ingredient);
                return;
            }
        }

        Ingredient newIngredient = new Ingredient(UUID.randomUUID().toString(), name, normalized, quantity, unit, expiryDate);
        normalizeUnits(newIngredient);
        pantryList.add(newIngredient);
    }

    public void deleteIngredient(String id) {
        for (int i = 0; i < pantryList.size(); i++) {
            if (pantryList.get(i).getId().equals(id)) {
                pantryList.remove(i);
                return;
            }
        }
    }

    public Ingredient getIngredientById(String id) {
        for (Ingredient ingredient : pantryList) {
            if (ingredient.getId().equals(id)) {
                return ingredient;
            }
        }
        return null;
    }

    public void updateIngredient(String id, String name, double quantity, String unit, String expiryDate) {
        deleteIngredient(id);
        addOrUpdateIngredient(name, quantity, unit, expiryDate);
    }

    private void aggregateQuantity(Ingredient existing, double addQty, String addUnit) {
        double currentQty = existing.getQuantity();
        String currentUnit = existing.getUnit();

        if (currentUnit.equals(addUnit)) {
            existing.setQuantity(currentQty + addQty);
        } else if (currentUnit.equals("kg") && addUnit.equals("g")) {
            existing.setQuantity(currentQty + (addQty / 1000.0));
        } else if (currentUnit.equals("g") && addUnit.equals("kg")) {
            existing.setQuantity(currentQty + (addQty * 1000.0));
        } else if (currentUnit.equals("L") && addUnit.equals("ml")) {
            existing.setQuantity(currentQty + (addQty / 1000.0));
        } else if (currentUnit.equals("ml") && addUnit.equals("L")) {
            existing.setQuantity(currentQty + (addQty * 1000.0));
        } else {
            existing.setQuantity(currentQty + addQty);
        }
    }

    private void updateToEarliestExpiry(Ingredient existing, String addExpiryDate) {
        String currentExpiry = existing.getExpiryDate();
        
        if (addExpiryDate == null || addExpiryDate.trim().isEmpty()) {
            return;
        }
        
        if (currentExpiry == null || currentExpiry.trim().isEmpty()) {
            existing.setExpiryDate(addExpiryDate);
            return;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = sdf.parse(currentExpiry);
            Date addDate = sdf.parse(addExpiryDate);
            
            if (addDate != null && currentDate != null && addDate.before(currentDate)) {
                existing.setExpiryDate(addExpiryDate);
            }
        } catch (ParseException e) {
            
        }
    }

    private void normalizeUnits(Ingredient ingredient) {
        double qty = ingredient.getQuantity();
        String unit = ingredient.getUnit();

        if (unit.equals("g") && qty >= 1000) {
            ingredient.setQuantity(qty / 1000.0);
            ingredient.setUnit("kg");
        } else if (unit.equals("ml") && qty >= 1000) {
            ingredient.setQuantity(qty / 1000.0);
            ingredient.setUnit("L");
        } else if (unit.equals("kg") && qty < 1.0 && qty > 0) {
            ingredient.setQuantity(qty * 1000.0);
            ingredient.setUnit("g");
        } else if (unit.equals("L") && qty < 1.0 && qty > 0) {
            ingredient.setQuantity(qty * 1000.0);
            ingredient.setUnit("ml");
        }
    }

    public String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String lower = name.trim().toLowerCase();
        
        if (lower.endsWith("oes")) {
            return lower.substring(0, lower.length() - 2);
        } else if (lower.endsWith("s") && !lower.endsWith("ss")) {
            return lower.substring(0, lower.length() - 1);
        }
        return lower;
    }
}
