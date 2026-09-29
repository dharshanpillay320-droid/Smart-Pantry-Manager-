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

    // Initializes the singleton and seeds the database if necessary
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

    // Checks for existing recipes and triggers seeding if empty
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

    // Populates the database with default core recipes
    private void seedDatabase() {
        addRecipe("Tomato Soup", "A warm and hearty tomato soup.", "1. Chop tomatoes and onions.\n2. Boil until soft.\n3. Blend until smooth.\n4. Salt to taste.", 
            new RecipeIngredient("Tomato", 500, "g"), 
            new RecipeIngredient("Onion", 1, "pieces"));

        addRecipe("Fluffy Omelette", "A simple and quick breakfast.", "1. Whisk eggs and milk.\n2. Fry in a pan until golden.\n3. Salt to taste.", 
            new RecipeIngredient("Egg", 3, "pieces"), 
            new RecipeIngredient("Milk", 50, "ml"));

        addRecipe("Pancakes", "Classic sweet pancakes.", "1. Mix flour, milk, eggs, and sugar.\n2. Pour batter onto hot pan.\n3. Flip when bubbly.", 
            new RecipeIngredient("Flour", 200, "g"), 
            new RecipeIngredient("Milk", 250, "ml"), 
            new RecipeIngredient("Egg", 2, "pieces"),
            new RecipeIngredient("Sugar", 30, "g"));

        addRecipe("Grilled Cheese", "Crispy and gooey sandwich.", "1. Place cheese between bread.\n2. Grill until melted.", 
            new RecipeIngredient("Bread", 2, "pieces"), 
            new RecipeIngredient("Cheese", 50, "g"));

        addRecipe("Scrambled Eggs", "Soft and creamy eggs.", "1. Whisk eggs with a splash of milk.\n2. Cook slowly in a pan.\n3. Salt to taste.", 
            new RecipeIngredient("Egg", 4, "pieces"), 
            new RecipeIngredient("Milk", 30, "ml"));

        addRecipe("Lemonade", "Refreshing summer drink.", "1. Squeeze lemons.\n2. Mix with water and sugar.", 
            new RecipeIngredient("Lemon", 3, "pieces"), 
            new RecipeIngredient("Sugar", 100, "g"));

        addRecipe("French Toast", "Sweet fried bread.", "1. Dip bread in whisked egg and milk.\n2. Fry until golden.", 
            new RecipeIngredient("Bread", 2, "pieces"), 
            new RecipeIngredient("Egg", 2, "pieces"), 
            new RecipeIngredient("Milk", 50, "ml"));

        addRecipe("Cheese Omelette", "Classic cheesy omelette.", "1. Whisk eggs and pour into pan.\n2. Add cheese and fold.\n3. Salt to taste.", 
            new RecipeIngredient("Egg", 3, "pieces"), 
            new RecipeIngredient("Cheese", 30, "g"));

        addRecipe("Tomato & Cheese Sandwich", "Fresh and quick sandwich.", "1. Slice tomato and cheese.\n2. Place between bread.\n3. Salt to taste.", 
            new RecipeIngredient("Bread", 2, "pieces"), 
            new RecipeIngredient("Tomato", 100, "g"), 
            new RecipeIngredient("Cheese", 40, "g"));

        addRecipe("Simple Flatbread", "Quick stovetop bread.", "1. Mix flour and milk into dough.\n2. Roll flat and fry in pan.\n3. Salt to taste.", 
            new RecipeIngredient("Flour", 200, "g"), 
            new RecipeIngredient("Milk", 100, "ml"));

        addRecipe("Onion Rings", "Crispy fried onions.", "1. Slice onions into rings.\n2. Dip in milk and flour.\n3. Fry until crispy.\n4. Salt to taste.", 
            new RecipeIngredient("Onion", 2, "pieces"), 
            new RecipeIngredient("Flour", 100, "g"), 
            new RecipeIngredient("Milk", 100, "ml"));

        addRecipe("Tomato & Onion Salad", "Fresh side salad.", "1. Chop tomato and onion.\n2. Toss together.\n3. Salt to taste.", 
            new RecipeIngredient("Tomato", 200, "g"), 
            new RecipeIngredient("Onion", 1, "pieces"));
    }

    // Inserts a new recipe and its child ingredients into the relational tables
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

    // Fetches and constructs all recipes and their ingredients from the database
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

    // Fetches ingredients associated with a specific recipe ID
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

    // Returns a list of recipes that have all required ingredients in the provided pantry list
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

    // Returns a list of recipes that are missing exactly one required ingredient from the pantry list
    public List<Recipe> getAlmostThereRecipes(List<Ingredient> pantry) {
        List<Recipe> all = getAllRecipes();
        List<Recipe> almostThere = new ArrayList<>();
        for (Recipe recipe : all) {
            if (isAlmostThere(recipe, pantry)) {
                almostThere.add(recipe);
            }
        }
        return almostThere;
    }

    // Fetches a single fully constructed recipe by ID
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

    // Evaluates if all ingredients for a recipe exist in the pantry with sufficient quantities
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

    // Evaluates if exactly one ingredient for a recipe is missing or insufficient in the pantry
    private boolean isAlmostThere(Recipe recipe, List<Ingredient> pantry) {
        int missingCount = 0;
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
                missingCount++;
            }
        }
        return missingCount == 1; 
    }

    // Compares quantity and handles unit conversions for requirements matching
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
