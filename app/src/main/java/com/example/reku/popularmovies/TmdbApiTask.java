package com.example.reku.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rakeshkalyankar on 31/12/15.
 */
public class TmdbApiTask extends AsyncTask<String, Void, String> {
    private Context context;
    private TextView textView_runtime;
    private MovieTilesAdapter movieTilesAdapter;
    private ArrayList<Movie> movieArrayList;
    private Boolean isMetaRequired;

    public TmdbApiTask(Context context, TextView runtime){
        this.context = context;
        this.textView_runtime = runtime;
        this.movieTilesAdapter = null;
        this.movieArrayList = null;
        this.isMetaRequired = true;
    }

    public TmdbApiTask(Context context, ArrayList<Movie> movieArrayList, MovieTilesAdapter movieTilesAdapter){
        this.context = context;
        this.textView_runtime = null;
        this.movieArrayList = movieArrayList;
        this.movieTilesAdapter = movieTilesAdapter;
        this.isMetaRequired = false;
    }

    private Uri buildUri(String param){
        final String APIKEY = context.getResources().getString(R.string.TMDbAPIKEY);
        if(isMetaRequired){
            return Uri.parse(Constants.META_BASE_URL + param).buildUpon()
                    .appendQueryParameter("api_key",APIKEY)
                    .build();
        }else{
            return Uri.parse(Constants.API_BASE_URL).buildUpon()
                    .appendQueryParameter("sort_by", Constants.getSortStringPath(param))
                    .appendQueryParameter("api_key", APIKEY)
                    .build();
        }
    }
    @Override
    protected String doInBackground(String... params) {
        if(params.length == 0){
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonstr = null;
        try {

            Uri uri = buildUri(params[0]);

            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                return null;
            }
            movieJsonstr = buffer.toString();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return movieJsonstr;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s != null) {
            if (isMetaRequired) {
                try {
                    JSONObject metaJson = new JSONObject(s);
                    String runtime = metaJson.getString("runtime");

                    // if view is destroyed before network operation is done
                    if (textView_runtime != null) {
                        textView_runtime.setText(runtime + "min");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                try {
                    ArrayList<Movie> movies = getPopularMoviesFromJson(s);
                    if(movies != null){
                        movieTilesAdapter.clear();
                        movieArrayList = movies;
                        for (Movie movie: movies){
                            movieTilesAdapter.add(movie);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<Movie> getPopularMoviesFromJson(String popularJsonStr) throws JSONException {
        if(popularJsonStr == null){
            return null;
        }
        final String RESULTS = "results";
        JSONObject receivedJson = new JSONObject(popularJsonStr);
        JSONArray receivedMoviesArray = receivedJson.getJSONArray(RESULTS);

        ArrayList<Movie> movies = new ArrayList<>();

        for(int i = 0; i < receivedMoviesArray.length(); i++ ){
            JSONObject movieJson = receivedMoviesArray.getJSONObject(i);
            movies.add(i, new Movie(movieJson));
        }

        return movies;
    }
}
