package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COL_ING_ID = "id";
    public static final String COL_ING_ORIGINAL_NAME = "originalName";
    public static final String COL_ING_NORMALIZED_NAME = "normalizedName";
    public static final String COL_ING_QUANTITY = "quantity";
    public static final String COL_ING_UNIT = "unit";
    public static final String COL_ING_EXPIRY = "expiryDate";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_REC_ID = "id";
    public static final String COL_REC_TITLE = "title";
    public static final String COL_REC_DESC = "description";
    public static final String COL_REC_INSTRUCTIONS = "instructions";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createIngredientsTable = "CREATE TABLE " + TABLE_INGREDIENTS + " ("
                + COL_ING_ID + " TEXT PRIMARY KEY, "
                + COL_ING_ORIGINAL_NAME + " TEXT, "
                + COL_ING_NORMALIZED_NAME + " TEXT, "
                + COL_ING_QUANTITY + " REAL, "
                + COL_ING_UNIT + " TEXT, "
                + COL_ING_EXPIRY + " TEXT)";
        db.execSQL(createIngredientsTable);

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_REC_ID + " TEXT PRIMARY KEY, "
                + COL_REC_TITLE + " TEXT, "
                + COL_REC_DESC + " TEXT, "
                + COL_REC_INSTRUCTIONS + " TEXT)";
        db.execSQL(createRecipesTable);

        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_RI_RECIPE_ID + " TEXT, "
                + COL_RI_NAME + " TEXT, "
                + COL_RI_QUANTITY + " REAL, "
                + COL_RI_UNIT + " TEXT, "
                + "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_REC_ID + "))";
        db.execSQL(createRecipeIngredientsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }
}
