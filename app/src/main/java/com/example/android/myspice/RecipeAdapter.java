package com.example.android.myspice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    ImageView recipeImage;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, 0, recipes);
    }


    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_card_view,parent,false);
        }

       Recipe items = getItem(position);

       String title = items.getTitle();
       String publisher = items.getPublisher();
       String Image = items.getImage();

        TextView recipeTitle = listItemView.findViewById(R.id.recipe_title);
        recipeTitle.setText(title);

        TextView recipePublisher = listItemView.findViewById(R.id.recipe_publisher);
        recipePublisher.setText(publisher);

        recipeImage = listItemView.findViewById(R.id.recipe_image);
        loadFromUrl(Image);

        return listItemView;

    }

    private void loadFromUrl(String url) {
        Picasso.get().load(url)
                .error(R.mipmap.ic_launcher)
                .into(recipeImage);
    }


}
