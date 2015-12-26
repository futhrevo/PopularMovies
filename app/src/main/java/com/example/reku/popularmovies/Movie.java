package com.example.reku.popularmovies;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rakeshkalyankar on 25/12/15.
 */
public class Movie {
    String poster_path;
    Boolean adult;
    String overview;
    String release_date;
    int[] genre_ids;
    String id;
    String original_title;
    String backdrop_path;
    Double vote_average;

    public Movie(JSONObject json) throws JSONException {
            this.poster_path = json.getString("poster_path");
            this.adult = json.getBoolean("adult");
            this.overview = json.getString("overview");
        this.release_date =json.getString("release_date");
        this.genre_ids = formatGenre(json.getJSONArray("genre_ids"));
        this.id = json.getString("id");
        this.original_title = json.getString("original_title");
        this.backdrop_path = json.getString("backdrop_path");
        this.vote_average = json.getDouble("vote_average");
    }

    private int[] formatGenre(JSONArray genreArray){
        if(genreArray == null){
            return null;
        }
        else{
            int[] numbers = new int[genreArray.length()];
            for(int i=0; i < genreArray.length();i++){
                numbers[i] = genreArray.optInt(i);
            }
            return numbers;
        }
    }

    private Uri getPoster_path(String size){
        String path = Constants.IMG_BASE_URL + size + this.poster_path;
        return Uri.parse(path);

    }

    private Uri getBackdrop_path(String size){
        String path = Constants.IMG_BASE_URL + size + this.backdrop_path;
        return Uri.parse(path);
    }

    // method to get poster path
    public Uri getPoster_path(){
        return getPoster_path(Constants.DEFAULT_POSTER_WIDTH);
    }

    public Uri getBackdrop_path(){
        return getBackdrop_path(Constants.DEFAULT_BACKDROP_WIDTH);
    }

}



//{
//        "poster_path":"\/fYzpM9GmpBlIC893fNjoWCwE24H.jpg",
//        "adult":false,
//        "overview":"Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.",
//        "release_date":"2015-12-18",
//        "genre_ids":[
//        28,
//        12,
//        878,
//        14
//        ],
//        "id":140607,
//        "original_title":"Star Wars: The Force Awakens",
//        "original_language":"en",
//        "title":"Star Wars: The Force Awakens",
//        "backdrop_path":"\/c2Ax8Rox5g6CneChwy1gmu4UbSb.jpg",
//        "popularity":84.513673,
//        "vote_count":973,
//        "video":false,
//        "vote_average":8.09
//        }

//  http://api.themoviedb.org/3/genre/movie/list?api_key=APIKEY