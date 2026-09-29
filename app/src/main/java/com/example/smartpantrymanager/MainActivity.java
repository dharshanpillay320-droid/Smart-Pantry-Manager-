package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPantry;
    private TextView tvEmptyPantry;
    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvPantry = findViewById(R.id.rvPantry);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        
        List<Ingredient> currentList = IngredientManager.getInstance(this).getPantryList();
        adapter = new IngredientAdapter(currentList, new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingredient ingredient) {
                showEditDeleteDialog(ingredient);
            }
        });
        rvPantry.setAdapter(adapter);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
                startActivity(intent);
            }
        });

        btnSuggestedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        checkExpiryWarning();
    }

    private void checkExpiryWarning() {
        if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOW_EXPIRY_WARNING", false)) {
            getIntent().removeExtra("EXTRA_SHOW_EXPIRY_WARNING");
            
            List<Ingredient> pantry = IngredientManager.getInstance(this).getPantryList();
            java.util.List<String> expiringSoonItems = new java.util.ArrayList<>();
            java.util.List<String> expiredItems = new java.util.ArrayList<>();
            
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                long now = new java.util.Date().getTime();
                
                for (Ingredient item : pantry) {
                    String exp = item.getExpiryDate();
                    if (exp != null && !exp.trim().isEmpty()) {
                        java.util.Date expDate = sdf.parse(exp);
                        long diffInMillies = expDate.getTime() - now;
                        long diffInDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS);
                        
                        if (diffInDays >= 0 && diffInDays <= 1) {
                            expiringSoonItems.add(item.getOriginalName());
                        } else if (diffInDays < 0) {
                            expiredItems.add(item.getOriginalName());
                        }
                    }
                }
            } catch (Exception e) {
            }
            
            if (!expiringSoonItems.isEmpty() || !expiredItems.isEmpty()) {
                android.content.SharedPreferences prefs = getSharedPreferences("SmartPantryPrefs", android.content.Context.MODE_PRIVATE);
                if (prefs.getBoolean("enable_expiry_alerts", true)) {
                    StringBuilder sb = new StringBuilder();
                    
                    if (!expiringSoonItems.isEmpty()) {
                        sb.append("Expiring today or tomorrow:\n");
                        for (String name : expiringSoonItems) {
                            sb.append("- ").append(name).append("\n");
                        }
                    }
                    
                    if (!expiredItems.isEmpty()) {
                        if (sb.length() > 0) sb.append("\n");
                        sb.append("Already expired:\n");
                        for (String name : expiredItems) {
                            sb.append("- ").append(name).append("\n");
                        }
                    }
                    
                    new AlertDialog.Builder(this)
                        .setTitle("Expiry Alert!")
                        .setMessage(sb.toString().trim())
                        .setPositiveButton("Got it", null)
                        .show();
                }
            }
        }
    }

    private void showEditDeleteDialog(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ingredient.getOriginalName());
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
                    intent.putExtra("EXTRA_INGREDIENT_ID", ingredient.getId());
                    startActivity(intent);
                } else if (which == 1) {
                    IngredientManager.getInstance(MainActivity.this).deleteIngredient(ingredient.getId());
                    onResume();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Ingredient> currentList = IngredientManager.getInstance(this).getPantryList();
        adapter.updateData(currentList);
        
        if (currentList.isEmpty()) {
            tvEmptyPantry.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            tvEmptyPantry.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
    }
}