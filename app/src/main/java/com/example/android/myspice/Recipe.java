package com.example.android.myspice;

public class Recipe {

   private String mTitle;
   private String mPublisher;
   private String mImage;
   private String mUrl;

    public Recipe(String title,String publisher,String image,String url){

         mTitle = title;
         mPublisher = publisher;
         mImage = image;
         mUrl = url;
    }

   public String getTitle(){ return mTitle; }
   public String getPublisher(){ return mPublisher; }
   public String getImage(){ return mImage; }
   public String getUrl(){ return mUrl; }
}
