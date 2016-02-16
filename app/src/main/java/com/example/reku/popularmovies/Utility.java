package com.example.reku.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by rakeshkalyankar on 13/02/16.
 */
public class Utility {

    // method for generating url for  async task
    public static String getSortStringPath(String type){
        if(type == "0")
            return "vote_average.desc";
        else{
            return "popularity.desc";
        }
    }

    // method get preference selected
    public static int getPrefSelected(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(settings.getString(context.getString(R.string.pref_sort_list_key), "1"));
    }

    public static void putPrefSelected(Context context, int num){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.pref_sort_list_key), String.valueOf(num)).apply();
    }

    public static String getYear(String date){
        String str[] = date.split("-");
        return str[0];
    }

    public static Uri getPoster_path(String size, String poster_path){
        String path = Constants.IMG_BASE_URL + size + poster_path;
        return Uri.parse(path);

    }
}
