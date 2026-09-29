package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // Defines callback interface for recipe item clicks
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    private List<Recipe> recipeList;
    private OnItemClickListener listener;

    // Initializes the adapter with recipe data and click handler
    public RecipeAdapter(List<Recipe> recipeList, OnItemClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    // Inflates the layout view for a single recipe list item
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    // Binds recipe data properties to the view holder elements
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvRecipeTitle.setText(recipe.getTitle());
        holder.tvRecipeDescription.setText(recipe.getDescription());

        // Attaches the click listener to the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(recipe);
                }
            }
        });
    }

    // Returns the total number of recipes in the dataset
    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    // Caches UI components for individual recipe list items
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeTitle;
        TextView tvRecipeDescription;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeDescription = itemView.findViewById(R.id.tvRecipeDescription);
        }
    }
}
