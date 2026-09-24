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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvRecipeDetailName = findViewById(R.id.tvRecipeDetailName);
        tvRecipeDetailIngredients = findViewById(R.id.tvRecipeDetailIngredients);
        tvRecipeDetailSteps = findViewById(R.id.tvRecipeDetailSteps);
        btnBackFromDetail = findViewById(R.id.btnBackFromDetail);

        if (getIntent() != null) {
            String name = getIntent().getStringExtra("EXTRA_RECIPE_NAME");
            String ingredients = getIntent().getStringExtra("EXTRA_RECIPE_INGREDIENTS");
            String steps = getIntent().getStringExtra("EXTRA_RECIPE_STEPS");

            if (name != null) tvRecipeDetailName.setText(name);
            if (ingredients != null) tvRecipeDetailIngredients.setText(ingredients);
            if (steps != null) tvRecipeDetailSteps.setText(steps);
        }

        btnBackFromDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
