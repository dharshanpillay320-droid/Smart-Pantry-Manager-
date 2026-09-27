package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etIngredientQuantity;
    private Spinner spnIngredientUnit;
    private EditText etExpiryDate;
    private ImageButton btnPickDate;
    private Button btnSaveIngredient;
    private Button btnCancel;
    private TextView tvAddEditTitle;
    
    private String editingId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etIngredientName = findViewById(R.id.etIngredientName);
        etIngredientQuantity = findViewById(R.id.etIngredientQuantity);
        spnIngredientUnit = findViewById(R.id.spnIngredientUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnCancel = findViewById(R.id.btnCancel);
        tvAddEditTitle = findViewById(R.id.tvAddEditTitle);

        if (getIntent() != null && getIntent().hasExtra("EXTRA_INGREDIENT_ID")) {
            editingId = getIntent().getStringExtra("EXTRA_INGREDIENT_ID");
            Ingredient ingredient = IngredientManager.getInstance(this).getIngredientById(editingId);
            if (ingredient != null) {
                tvAddEditTitle.setText("Edit Ingredient");
                etIngredientName.setText(ingredient.getOriginalName());
                
                String qtyStr = String.valueOf(ingredient.getQuantity());
                if (qtyStr.endsWith(".0")) {
                    qtyStr = qtyStr.substring(0, qtyStr.length() - 2);
                }
                etIngredientQuantity.setText(qtyStr);
                
                if (ingredient.getExpiryDate() != null) {
                    etExpiryDate.setText(ingredient.getExpiryDate());
                }
                
                String[] units = getResources().getStringArray(R.array.metric_units);
                for (int i = 0; i < units.length; i++) {
                    if (units[i].equals(ingredient.getUnit())) {
                        spnIngredientUnit.setSelection(i);
                        break;
                    }
                }
            }
        }

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditIngredientActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                                etExpiryDate.setText(formattedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etIngredientName.getText().toString().trim();
                String qtyString = etIngredientQuantity.getText().toString().trim();
                String expiry = etExpiryDate.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(AddEditIngredientActivity.this, "Ingredient name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (qtyString.isEmpty()) {
                    Toast.makeText(AddEditIngredientActivity.this, "Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                double quantity = 0;
                try {
                    quantity = Double.parseDouble(qtyString);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddEditIngredientActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity <= 0) {
                    Toast.makeText(AddEditIngredientActivity.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                String unit = spnIngredientUnit.getSelectedItem().toString();

                if (editingId != null) {
                    IngredientManager.getInstance(AddEditIngredientActivity.this).updateIngredient(editingId, name, quantity, unit, expiry);
                } else {
                    IngredientManager.getInstance(AddEditIngredientActivity.this).addOrUpdateIngredient(name, quantity, unit, expiry);
                }
                
                finish();
            }
        });
    }
}
