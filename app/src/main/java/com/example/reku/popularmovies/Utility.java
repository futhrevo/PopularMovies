package com.example.reku.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
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
}
