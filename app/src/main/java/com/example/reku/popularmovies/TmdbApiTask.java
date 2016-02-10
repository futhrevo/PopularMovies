package com.example.reku.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;

/**
 * Created by rakeshkalyankar on 31/12/15.
 */
public class TmdbApiTask extends AsyncTask<String, Void, String> {
    private Context context;
    private TextView textView_runtime;
    private ListView listView_extra;
    private MovieTilesAdapter movieTilesAdapter;
    private ArrayList<Movie> movieArrayList;
    private Boolean isMetaRequired;
    private int type;

    // constructor to fetch movie run length
    public TmdbApiTask(Context context, TextView runtime){
        this.context = context;
        this.textView_runtime = runtime;
        this.movieTilesAdapter = null;
        this.movieArrayList = null;
        this.isMetaRequired = true;
        this.type = Constants.FETCH_METAINFO;
    }

    //constructor to fetch movie trailers and reviews contained as list
    public TmdbApiTask(Context context, ListView listView, int type){
        this.context = context;
        this.textView_runtime = null;
        this.movieTilesAdapter = null;
        this.movieArrayList = null;
        this.isMetaRequired = true;
        this.type = type;
        this.listView_extra = listView;
    }

    //constructor to fetch movies list
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
            switch (type) {
                case Constants.FETCH_METAINFO:
                    return Uri.parse(Constants.META_BASE_URL + param).buildUpon()
                            .appendQueryParameter("api_key", APIKEY)
                            .build();
                case Constants.FETCH_VIDEOS:
                    return Uri.parse(Constants.META_BASE_URL + param).buildUpon()
                            .appendPath("videos")
                            .appendQueryParameter("api_key", APIKEY)
                            .build();
                case Constants.FETCH_REVIEWS:
                    return Uri.parse(Constants.META_BASE_URL + param).buildUpon()
                            .appendPath("reviews")
                            .appendQueryParameter("api_key", APIKEY)
                            .build();
                default:
                    return null;
            }
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

            //check if no uri is returned
            if(uri == null){
                return null;
            }

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
                switch (type) {
                    case Constants.FETCH_METAINFO:
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
                        break;
                    case Constants.FETCH_VIDEOS:
                        try {
                            JSONObject metaJson = new JSONObject(s);
                            JSONArray results = metaJson.getJSONArray("results");
                            ArrayList<HashMap<String , String>> trailers = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                HashMap<String, String> trailer = new HashMap<>();
                                JSONObject obj =  results.getJSONObject(i);
                                String key = obj.getString("key");
                                String name = obj.getString("name");
                                trailer.put("name", name);
                                trailer.put("key", key);
                                trailers.add(trailer);
                            }

                            SimpleAdapter listAdapter = new SimpleAdapter(context, trailers, R.layout.list_item_trailer,
                                    new String[]{"name", "key"},
                                    new int[]{R.id.textView_listitem_trailer_title, R.id.textView_trailer_key} );
                            listView_extra.setAdapter(listAdapter);
                            Constants.setListViewHeightBasedOnChildren(listView_extra);
                            listView_extra.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selected = ((TextView) view.findViewById(R.id.textView_trailer_key)).getText().toString();
                                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+selected));
                                    context.startActivity(youtubeIntent);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.FETCH_REVIEWS:
                        try {
                            JSONObject metaJson = new JSONObject(s);
                            JSONArray results = metaJson.getJSONArray("results");
                           ArrayList<HashMap<String , String>> reviews = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                HashMap<String, String> review = new HashMap<>();
                                JSONObject obj =  results.getJSONObject(i);
                                String author = obj.getString("author");
                                String content = obj.getString("content");
                                review.put("author", author);
                                review.put("content", content);
                                reviews.add(review);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(context,reviews, R.layout.list_item_review, new String[]{"author", "content"}, new int[]{R.id.textView_author, R.id.textView_review});
                            listView_extra.setAdapter(adapter);
                            Constants.setListViewHeightBasedOnChildren(listView_extra);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
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
