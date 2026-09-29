package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    // Defines the callback interface for item selection events
    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }

    private List<Ingredient> ingredientList;
    private OnItemClickListener listener;

    public IngredientAdapter(List<Ingredient> ingredientList, OnItemClickListener listener) {
        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    // Updates the dataset and refreshes the view
    public void updateData(List<Ingredient> newList) {
        this.ingredientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    // Binds ingredient data to the UI components
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.tvItemName.setText(ingredient.getOriginalName());
        
        // Formats the quantity string to remove trailing decimal zeroes
        String quantityStr = String.valueOf(ingredient.getQuantity());
        if (quantityStr.endsWith(".0")) {
            quantityStr = quantityStr.substring(0, quantityStr.length() - 2);
        }
        
        holder.tvItemQuantity.setText(quantityStr + " " + ingredient.getUnit());
        
        // changes the expiry date and updates the text color based on how close it is to expiration
        if (ingredient.getExpiryDate() != null && !ingredient.getExpiryDate().trim().isEmpty()) {
            holder.tvItemExpiry.setText("Exp: " + ingredient.getExpiryDate());
            
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date expDate = sdf.parse(ingredient.getExpiryDate());
                long diffInMillies = expDate.getTime() - new java.util.Date().getTime();
                long diffInDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS);
                
                if (diffInDays <= 2) {
                    holder.tvItemExpiry.setTextColor(android.graphics.Color.RED);
                } else if (diffInDays <= 5) {
                    holder.tvItemExpiry.setTextColor(android.graphics.Color.parseColor("#FFA500")); 
                } else {
                    holder.tvItemExpiry.setTextColor(holder.defaultExpiryColor);
                }
            } catch (Exception e) {
                holder.tvItemExpiry.setTextColor(holder.defaultExpiryColor);
            }
        } else {
            holder.tvItemExpiry.setText("");
            holder.tvItemExpiry.setTextColor(holder.defaultExpiryColor);
        }

        // Sets the click listener for the entire item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(ingredient);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    // View holder for caching UI component references
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemQuantity;
        TextView tvItemExpiry;
        int defaultExpiryColor;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemExpiry = itemView.findViewById(R.id.tvItemExpiry);
            
            // Caches the default text color to support dynamic recoloring
            defaultExpiryColor = tvItemExpiry.getCurrentTextColor();
        }
    }
}
