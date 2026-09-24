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

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvSuggestedRecipes;
    private TextView tvNoRecipes;
    private Button btnBackToPantry;

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

    public void openRecipeDetail(String recipeName, String ingredients, String steps) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("EXTRA_RECIPE_NAME", recipeName);
        intent.putExtra("EXTRA_RECIPE_INGREDIENTS", ingredients);
        intent.putExtra("EXTRA_RECIPE_STEPS", steps);
        startActivity(intent);
    }
}
