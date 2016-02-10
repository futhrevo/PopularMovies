package com.example.reku.popularmovies;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by rakeshkalyankar on 26/12/15.
 */
public final class Constants {

    public Constants(){
        Log.i("Constants", "Constants constructor");
    }

    public static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    public static final String META_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final int POSTER_WIDTH = 500;
    public static final int BACKDROP_WIDTH = 780;
    public static final String DEFAULT_POSTER_WIDTH = "w" + POSTER_WIDTH;
    public static final String DEFAULT_BACKDROP_WIDTH = "w" + BACKDROP_WIDTH;
    public static final String SAVED_INST_KEY_MP_FRAG = "moviesMostPopular";
    public static final String SAVED_INST_KEY_UR_FRAG = "moviesUserRating";
    public static final String SELECTED_MOVIE_INTENT = "selectedMovieIntent";

    public static final int FETCH_METAINFO = 1;
    public static final int FETCH_VIDEOS = 2;
    public static final int FETCH_REVIEWS = 3;

    public static String getSortStringPath(String type){
        if(type == "0")
            return "vote_average.desc";
        else{
            return "popularity.desc";
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
