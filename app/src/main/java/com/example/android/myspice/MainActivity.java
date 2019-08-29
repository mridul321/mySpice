package com.example.android.myspice;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ChipGroup chipGroup;
    private RecipeAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private Chip entryChip;
    private Button button;
    private View loadingIndicator;
    private String textQuery;
    private HorizontalScrollView horizontalScrollView;
    private  SearchView searchIngredient;
    public static final String LOG_TAG = MainActivity.class.getName();
    private static String RECIPE_REQUEST_URL =
            "https://www.food2fork.com/api/search?key=e042d9923e3de5a280eb235b44ea8bac&q=eggs,onion,tomato&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        listView = findViewById(R.id.recipe_view);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);
        searchIngredient = findViewById(R.id.editText);
        chipGroup = findViewById(R.id.entry_chip_group);
        button = findViewById(R.id.button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        horizontalScrollView = findViewById(R.id.scroll_view);


        mAdapter = new RecipeAdapter(this,new ArrayList<Recipe>());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe index = mAdapter.getItem(position);

                Uri recipeUri = Uri.parse(index.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW,recipeUri);

                startActivity(websiteIntent);
            }
        });

          textQuery = "";
          //horizontal auto scroll and cursor jump functionality and load next page.
          button.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.gray));
          searchIngredient.requestFocus();

        searchIngredient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String query) {
                  entryChip = getChip(chipGroup,query);
                  chipGroup.addView(entryChip);
                  textQuery=textQuery+submitQuery(query);
                  searchIngredient.setQuery("",false);
                  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                  return true;

              }

              @Override
              public boolean onQueryTextChange(String newText) {
                  return false;
              }
          });





         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 if(textQuery.equals("")){
                     Toast.makeText(MainActivity.this, "No Ingredient Chip entered", Toast.LENGTH_SHORT).show();

                 }
                 else if(!textQuery.equals("")){
                     InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                     loadingIndicator.setVisibility(View.VISIBLE);
                     textQuery = textQuery.substring(0,textQuery.length()-1);
                     RECIPE_REQUEST_URL = "https://www.food2fork.com/api/search?key=e042d9923e3de5a280eb235b44ea8bac&q="+textQuery+"&page=1";
                     Log.i("tag",RECIPE_REQUEST_URL);
                     foodAsyncTask task = new foodAsyncTask();
                     task.execute(RECIPE_REQUEST_URL);

                 }

             }
         });

    }

    private String submitQuery(String parameters) {
        parameters=parameters+",";
        return parameters;
    }






    private Chip getChip(final ChipGroup entryChipGroup, final String text) {
        final Chip chip = new Chip(this);
        chip.setChipDrawable(ChipDrawable.createFromResource(this,R.xml.chips));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryChipGroup.removeView(chip);
                textQuery = textQuery.replace(text,"");
                editQuery(text);
            }
        });
        return chip;
    }

    private String editQuery(String text) {
        //if-else cases to implement chip add/remove functionality
     return textQuery;
    }


    private class foodAsyncTask extends AsyncTask<String,Void, List<Recipe>>{

        @Override
        protected List<Recipe> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Recipe> result = QueryUtills.fetchRecipeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Recipe> data) {


            loadingIndicator.setVisibility(View.GONE);

            mAdapter.clear();

            if(data != null && !data.isEmpty()){
                mAdapter.addAll(data);
            }

            mEmptyStateTextView.setText(R.string.no_recipe);
        }
    }
}

