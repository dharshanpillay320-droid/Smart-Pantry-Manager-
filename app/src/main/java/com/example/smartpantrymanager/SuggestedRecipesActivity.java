package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvSuggestedRecipes;
    private TextView tvNoRecipes;
    private Button btnBackToPantry;
    private RecipeAdapter adapter;

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
        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        rvSuggestedRecipes.setLayoutManager(new LinearLayoutManager(this));

        btnBackToPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        List<Ingredient> pantry = IngredientManager.getInstance(this).getPantryList();
        List<Recipe> matchedRecipes = RecipeManager.getInstance(this).getMatchedRecipes(pantry);

        if (matchedRecipes.isEmpty()) {
            tvNoRecipes.setVisibility(View.VISIBLE);
            rvSuggestedRecipes.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            rvSuggestedRecipes.setVisibility(View.VISIBLE);
            
            adapter = new RecipeAdapter(matchedRecipes, new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Recipe recipe) {
                    Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                    intent.putExtra("EXTRA_RECIPE_ID", recipe.getId());
                    startActivity(intent);
                }
            });
            rvSuggestedRecipes.setAdapter(adapter);
        }
    }
}
