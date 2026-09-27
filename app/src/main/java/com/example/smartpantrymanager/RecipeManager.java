package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeManager {
    private static RecipeManager instance;
    private DatabaseHelper dbHelper;
    private Context context;

    private RecipeManager(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = new DatabaseHelper(this.context);
        checkAndSeedRecipes();
    }

    public static RecipeManager getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeManager(context);
        }
        return instance;
    }

    private void checkAndSeedRecipes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + DatabaseHelper.TABLE_RECIPES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            seedDatabase();
        }
    }

    private void seedDatabase() {
        // We will add 15 recipes as requested.
        addRecipe("Tomato Soup", "A warm and hearty tomato soup.", "1. Chop tomatoes and onions.\n2. Boil until soft.\n3. Blend until smooth.", 
            new RecipeIngredient("Tomato", 500, "g"), 
            new RecipeIngredient("Onion", 1, "pieces"));

        addRecipe("Simple Pasta", "Quick tomato pasta.", "1. Boil pasta.\n2. Make tomato sauce.\n3. Mix and serve.", 
            new RecipeIngredient("Pasta", 250, "g"), 
            new RecipeIngredient("Tomato", 200, "g"));

        addRecipe("Fluffy Omelette", "A simple and quick breakfast.", "1. Whisk eggs and milk.\n2. Fry in a pan until golden.", 
            new RecipeIngredient("Egg", 3, "pieces"), 
            new RecipeIngredient("Milk", 50, "ml"));

        addRecipe("Chicken Salad", "Healthy grilled chicken salad.", "1. Grill chicken.\n2. Chop lettuce and tomatoes.\n3. Mix everything.", 
            new RecipeIngredient("Chicken", 200, "g"), 
            new RecipeIngredient("Lettuce", 100, "g"), 
            new RecipeIngredient("Tomato", 100, "g"));

        addRecipe("Pancakes", "Classic sweet pancakes.", "1. Mix flour, milk, and eggs.\n2. Pour batter onto hot pan.\n3. Flip when bubbly.", 
            new RecipeIngredient("Flour", 200, "g"), 
            new RecipeIngredient("Milk", 250, "ml"), 
            new RecipeIngredient("Egg", 2, "pieces"));

        addRecipe("Beef Stew", "Rich and slow-cooked beef stew.", "1. Brown beef chunks.\n2. Add potatoes and carrots.\n3. Simmer for 2 hours.", 
            new RecipeIngredient("Beef", 500, "g"), 
            new RecipeIngredient("Potato", 300, "g"), 
            new RecipeIngredient("Carrot", 200, "g"));

        addRecipe("Mashed Potatoes", "Creamy side dish.", "1. Boil potatoes until soft.\n2. Mash with butter and milk.", 
            new RecipeIngredient("Potato", 500, "g"), 
            new RecipeIngredient("Butter", 50, "g"), 
            new RecipeIngredient("Milk", 100, "ml"));

        addRecipe("Grilled Cheese", "Crispy and gooey sandwich.", "1. Butter bread.\n2. Add cheese.\n3. Grill until melted.", 
            new RecipeIngredient("Bread", 2, "pieces"), 
            new RecipeIngredient("Cheese", 50, "g"), 
            new RecipeIngredient("Butter", 15, "g"));

        addRecipe("Oatmeal", "Warm breakfast oats.", "1. Boil milk.\n2. Stir in oats.\n3. Cook until thick.", 
            new RecipeIngredient("Oats", 50, "g"), 
            new RecipeIngredient("Milk", 200, "ml"));

        addRecipe("Lemonade", "Refreshing summer drink.", "1. Squeeze lemons.\n2. Mix with water and sugar.", 
            new RecipeIngredient("Lemon", 3, "pieces"), 
            new RecipeIngredient("Sugar", 100, "g"), 
            new RecipeIngredient("Water", 1, "L"));

        addRecipe("Fried Rice", "Quick egg fried rice.", "1. Fry eggs.\n2. Add cooked rice and soy sauce.\n3. Stir fry.", 
            new RecipeIngredient("Rice", 200, "g"), 
            new RecipeIngredient("Egg", 2, "pieces"), 
            new RecipeIngredient("Soy Sauce", 30, "ml"));

        addRecipe("Garlic Bread", "Crispy oven-baked garlic bread.", "1. Crush garlic into butter.\n2. Spread on bread.\n3. Bake until crispy.", 
            new RecipeIngredient("Bread", 4, "pieces"), 
            new RecipeIngredient("Butter", 40, "g"), 
            new RecipeIngredient("Garlic", 2, "pieces"));

        addRecipe("Fruit Smoothie", "Blended fruit drink.", "1. Chop fruit.\n2. Blend with milk and yogurt.", 
            new RecipeIngredient("Banana", 1, "pieces"), 
            new RecipeIngredient("Milk", 200, "ml"), 
            new RecipeIngredient("Yogurt", 100, "g"));

        addRecipe("Scrambled Eggs", "Soft and creamy eggs.", "1. Whisk eggs with a splash of milk.\n2. Cook slowly in a buttered pan.", 
            new RecipeIngredient("Egg", 4, "pieces"), 
            new RecipeIngredient("Butter", 20, "g"), 
            new RecipeIngredient("Milk", 30, "ml"));

        addRecipe("Vegetable Stir Fry", "Quick healthy veggies.", "1. Chop broccoli and carrots.\n2. Stir fry with soy sauce.", 
            new RecipeIngredient("Broccoli", 200, "g"), 
            new RecipeIngredient("Carrot", 150, "g"), 
            new RecipeIngredient("Soy Sauce", 40, "ml"));
    }

    private void addRecipe(String title, String desc, String instr, RecipeIngredient... ingredients) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String recipeId = UUID.randomUUID().toString();
        
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_REC_ID, recipeId);
        recipeValues.put(DatabaseHelper.COL_REC_TITLE, title);
        recipeValues.put(DatabaseHelper.COL_REC_DESC, desc);
        recipeValues.put(DatabaseHelper.COL_REC_INSTRUCTIONS, instr);
        db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (RecipeIngredient ri : ingredients) {
            ContentValues riValues = new ContentValues();
            riValues.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            riValues.put(DatabaseHelper.COL_RI_NAME, ri.getName());
            riValues.put(DatabaseHelper.COL_RI_QUANTITY, ri.getQuantity());
            riValues.put(DatabaseHelper.COL_RI_UNIT, ri.getUnit());
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, riValues);
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor rCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_RECIPES, null);

        if (rCursor.moveToFirst()) {
            do {
                String id = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_ID));
                String title = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_TITLE));
                String desc = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_DESC));
                String instr = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_INSTRUCTIONS));

                List<RecipeIngredient> ingredients = getIngredientsForRecipe(id, db);
                recipes.add(new Recipe(id, title, desc, ingredients, instr));
            } while (rCursor.moveToNext());
        }
        rCursor.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(String recipeId, SQLiteDatabase db) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_RECIPE_INGREDIENTS + " WHERE " + DatabaseHelper.COL_RI_RECIPE_ID + "=?", new String[]{recipeId});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_UNIT));
                list.add(new RecipeIngredient(name, qty, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Recipe> getMatchedRecipes(List<Ingredient> pantry) {
        List<Recipe> all = getAllRecipes();
        List<Recipe> matched = new ArrayList<>();
        for (Recipe recipe : all) {
            if (canMakeRecipe(recipe, pantry)) {
                matched.add(recipe);
            }
        }
        return matched;
    }

    public Recipe getRecipeById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_RECIPES + " WHERE " + DatabaseHelper.COL_REC_ID + "=?", new String[]{id});
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_DESC));
            String instr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REC_INSTRUCTIONS));
            List<RecipeIngredient> ingredients = getIngredientsForRecipe(id, db);
            cursor.close();
            return new Recipe(id, title, desc, ingredients, instr);
        }
        if (cursor != null) cursor.close();
        return null;
    }

    private boolean canMakeRecipe(Recipe recipe, List<Ingredient> pantry) {
        for (RecipeIngredient required : recipe.getRequiredIngredients()) {
            boolean found = false;
            String normalizedRequiredName = IngredientManager.getInstance(context).normalizeName(required.getName());

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
