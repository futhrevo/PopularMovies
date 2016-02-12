package com.example.reku.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.reku.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rakeshkalyankar on 25/12/15.
 */
public class Movie implements Parcelable{
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

    public Movie(Cursor cursor){
        int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        this.poster_path = cursor.getString(posterIndex);
    }
    public Movie(Parcel in) {
        poster_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        release_date = in.readString();
        genre_ids = in.createIntArray();
        id = in.readString();
        original_title = in.readString();
        backdrop_path = in.readString();
        vote_average = in.readDouble();
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

    public String getYear(){
        String str[] = this.release_date.split("-");
        return str[0];
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeIntArray(genre_ids);
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(backdrop_path);
        dest.writeDouble(vote_average);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
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