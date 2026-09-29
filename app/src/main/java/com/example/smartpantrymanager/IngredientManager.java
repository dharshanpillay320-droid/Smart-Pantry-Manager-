package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IngredientManager {
    private static IngredientManager instance;
    private DatabaseHelper dbHelper;

    private IngredientManager(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static IngredientManager getInstance(Context context) {
        if (instance == null) {
            instance = new IngredientManager(context);
        }
        return instance;
    }

    // Fetches and constructs all ingredients from the database
    public List<Ingredient> getPantryList() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_INGREDIENTS, null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_ID));
                String oName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_ORIGINAL_NAME));
                String nName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_NORMALIZED_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_UNIT));
                String exp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_EXPIRY));

                list.add(new Ingredient(id, oName, nName, qty, unit, exp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Handles logic for adding a new ingredient or aggregating its data if it already exists
    public void addOrUpdateIngredient(String name, double quantity, String unit, String expiryDate) {
        String normalized = normalizeName(name);
        List<Ingredient> pantryList = getPantryList();

        for (Ingredient ingredient : pantryList) {
            if (ingredient.getNormalizedName().equals(normalized)) {
                aggregateQuantity(ingredient, quantity, unit);
                updateToEarliestExpiry(ingredient, expiryDate);
                normalizeUnits(ingredient);
                saveIngredientToDb(ingredient);
                return;
            }
        }

        Ingredient newIngredient = new Ingredient(UUID.randomUUID().toString(), name, normalized, quantity, unit, expiryDate);
        normalizeUnits(newIngredient);
        insertIngredientToDb(newIngredient);
    }

    // Executes a SQL insert for a new ingredient
    private void insertIngredientToDb(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ING_ID, ingredient.getId());
        values.put(DatabaseHelper.COL_ING_ORIGINAL_NAME, ingredient.getOriginalName());
        values.put(DatabaseHelper.COL_ING_NORMALIZED_NAME, ingredient.getNormalizedName());
        values.put(DatabaseHelper.COL_ING_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseHelper.COL_ING_UNIT, ingredient.getUnit());
        values.put(DatabaseHelper.COL_ING_EXPIRY, ingredient.getExpiryDate());
        db.insert(DatabaseHelper.TABLE_INGREDIENTS, null, values);
    }

    // Executes a SQL update for an existing ingredient
    private void saveIngredientToDb(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ING_ORIGINAL_NAME, ingredient.getOriginalName());
        values.put(DatabaseHelper.COL_ING_NORMALIZED_NAME, ingredient.getNormalizedName());
        values.put(DatabaseHelper.COL_ING_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseHelper.COL_ING_UNIT, ingredient.getUnit());
        values.put(DatabaseHelper.COL_ING_EXPIRY, ingredient.getExpiryDate());
        db.update(DatabaseHelper.TABLE_INGREDIENTS, values, DatabaseHelper.COL_ING_ID + "=?", new String[]{ingredient.getId()});
    }

    // Executes a SQL deletion for a specific ingredient
    public void deleteIngredient(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_INGREDIENTS, DatabaseHelper.COL_ING_ID + "=?", new String[]{id});
    }

    // Queries the database to return a specific ingredient by its ID
    public Ingredient getIngredientById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_INGREDIENTS, null, DatabaseHelper.COL_ING_ID + "=?", new String[]{id}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            String oName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_ORIGINAL_NAME));
            String nName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_NORMALIZED_NAME));
            double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_QUANTITY));
            String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_UNIT));
            String exp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ING_EXPIRY));
            cursor.close();
            return new Ingredient(id, oName, nName, qty, unit, exp);
        }
        
        if (cursor != null) cursor.close();
        return null;
    }

    // Deletes an old ingredient record and replaces it with a new state
    public void updateIngredient(String id, String name, double quantity, String unit, String expiryDate) {
        deleteIngredient(id);
        addOrUpdateIngredient(name, quantity, unit, expiryDate);
    }

    // Converts and adds an incoming quantity to an existing ingredient quantity based on unit differences
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

    // Compares two expiry dates and applies the earliest date to the ingredient
    private void updateToEarliestExpiry(Ingredient existing, String addExpiryDate) {
        String currentExpiry = existing.getExpiryDate();
        
        if (addExpiryDate == null || addExpiryDate.trim().isEmpty()) return;
        
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
            // Fails safe to existing date on parse exception
        }
    }

    // Auto-scales ingredient units based on threshold crossings
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

    // Standardizes casing and strips plural suffixes for string matching
    public String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        
        String lower = name.trim().toLowerCase();
        if (lower.endsWith("oes")) return lower.substring(0, lower.length() - 2);
        else if (lower.endsWith("s") && !lower.endsWith("ss")) return lower.substring(0, lower.length() - 1);
        
        return lower;
    }
}
