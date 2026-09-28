package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvSuggestedRecipes;
    private TextView tvNoRecipes;
    private LinearLayout llAlmostThere;
    private Spinner spnAlmostThere;
    private Button btnBackToPantry;
    private RecipeAdapter adapter;

    private List<Recipe> matchedRecipes;
    private List<Recipe> almostThereRecipes;

    private boolean isSpinnerInitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvSuggestedRecipes = findViewById(R.id.rvSuggestedRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);
        llAlmostThere = findViewById(R.id.llAlmostThere);
        spnAlmostThere = findViewById(R.id.spnAlmostThere);
        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        rvSuggestedRecipes.setLayoutManager(new LinearLayoutManager(this));

        btnBackToPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        matchedRecipes = new ArrayList<>();
        adapter = new RecipeAdapter(matchedRecipes, new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                openRecipe(recipe.getId());
            }
        });
        rvSuggestedRecipes.setAdapter(adapter);

        spnAlmostThere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInitial) {
                    isSpinnerInitial = false;
                    return;
                }
                
                if (position > 0 && almostThereRecipes != null) {
                    Recipe selected = almostThereRecipes.get(position - 1);
                    openRecipe(selected.getId());
                    spnAlmostThere.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void openRecipe(String recipeId) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_ID", recipeId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        List<Ingredient> pantry = IngredientManager.getInstance(this).getPantryList();
        
        List<Recipe> newMatched = RecipeManager.getInstance(this).getMatchedRecipes(pantry);
        matchedRecipes.clear();
        matchedRecipes.addAll(newMatched);
        adapter.notifyDataSetChanged();
        
        if (matchedRecipes.isEmpty()) {
            tvNoRecipes.setVisibility(View.VISIBLE);
            rvSuggestedRecipes.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            rvSuggestedRecipes.setVisibility(View.VISIBLE);
        }

        almostThereRecipes = RecipeManager.getInstance(this).getAlmostThereRecipes(pantry);

        if (almostThereRecipes.isEmpty()) {
            llAlmostThere.setVisibility(View.GONE);
        } else {
            llAlmostThere.setVisibility(View.VISIBLE);
            
            List<String> spinnerItems = new ArrayList<>();
            spinnerItems.add("Select a recipe to view...");
            for (Recipe r : almostThereRecipes) {
                spinnerItems.add(r.getTitle());
            }
            
            isSpinnerInitial = true;
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAlmostThere.setAdapter(spinnerAdapter);
        }
    }
}
