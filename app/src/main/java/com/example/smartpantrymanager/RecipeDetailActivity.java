package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailName;
    private TextView tvRecipeDetailIngredients;
    private TextView tvRecipeDetailSteps;
    private Button btnBackFromDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup screen layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link UI elements
        tvRecipeDetailName = findViewById(R.id.tvRecipeDetailName);
        tvRecipeDetailIngredients = findViewById(R.id.tvRecipeDetailIngredients);
        tvRecipeDetailSteps = findViewById(R.id.tvRecipeDetailSteps);
        btnBackFromDetail = findViewById(R.id.btnBackFromDetail);

        // Fetch and display recipe details
        if (getIntent() != null && getIntent().hasExtra("EXTRA_RECIPE_ID")) {
            String recipeId = getIntent().getStringExtra("EXTRA_RECIPE_ID");
            Recipe recipe = RecipeManager.getInstance(this).getRecipeById(recipeId);
            
            if (recipe != null) {
                tvRecipeDetailName.setText(recipe.getTitle());
                tvRecipeDetailSteps.setText(recipe.getInstructions());
                
                // Format ingredients list
                StringBuilder ingredientsBuilder = new StringBuilder();
                for (RecipeIngredient ri : recipe.getRequiredIngredients()) {
                    
                    String qtyStr = String.valueOf(ri.getQuantity());
                    if (qtyStr.endsWith(".0")) {
                        qtyStr = qtyStr.substring(0, qtyStr.length() - 2);
                    }
                    
                    ingredientsBuilder.append("- ")
                                      .append(ri.getName())
                                      .append(" (")
                                      .append(qtyStr)
                                      .append(" ")
                                      .append(ri.getUnit())
                                      .append(")\n");
                }
                tvRecipeDetailIngredients.setText(ingredientsBuilder.toString().trim());
            }
        }

        // Handle back navigation
        btnBackFromDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
