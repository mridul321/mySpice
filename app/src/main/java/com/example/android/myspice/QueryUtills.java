package com.example.android.myspice;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.myspice.MainActivity.LOG_TAG;

public final class QueryUtills {

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);                                                                       //Returns New Url object from the given String
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }                                                                                                   //Takes the Url object and makes request to the server
        //returns a String as Response
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);                                                 //The returned response reads from input stream
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();                                                     //Reads from the input Stream line by line and returns String
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

   private static List<Recipe> extractFeatureFromJson(String recipeJSON){

       ArrayList<Recipe> recipeArrayList = new ArrayList<>();
       String title,publisher,imageUrl,sourceUrl;

       try{

           JSONObject jsonRootObject = new JSONObject(recipeJSON);
           JSONArray jsonArray =jsonRootObject.optJSONArray("recipes");
           for (int i=0;i<jsonArray.length();i++){
               JSONObject jsonObject = jsonArray.getJSONObject(i);

               title = jsonObject.optString("title");
               publisher = jsonObject.optString("publisher");
               imageUrl = jsonObject.optString("image_url");
               sourceUrl = jsonObject.optString("source_url");

             Recipe recipe = new Recipe(title,publisher,imageUrl,sourceUrl);
             recipeArrayList.add(recipe);


           }

       }catch (JSONException e){
           Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
       }
       return recipeArrayList;
   }

    public static List<Recipe> fetchRecipeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {                                                                                               //Made public so that it can be accessed by async task
            // in the earthquake activity class.
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        List<Recipe> recipes = extractFeatureFromJson(jsonResponse);


        return recipes;
    }



}
