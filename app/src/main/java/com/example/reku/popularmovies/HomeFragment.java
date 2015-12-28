package com.example.reku.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {
    private final String TAG = HomeFragment.class.getSimpleName();
    private MovieTilesAdapter movieTilesAdapter;
    private ArrayList<Movie> movieArrayList;
    private Integer state ;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortBy = Integer.parseInt(settings.getString(getString(R.string.pref_sort_list_key),"1"));
        // if savedInstanceState is null, perform full fetch
        // else savedInstanceState contained key not equal to sort by, perform full fetch
        //else use from savedInstanceState
        if (savedInstanceState != null ) {
            if(sortBy == 0){
                if(savedInstanceState.containsKey(Constants.SAVED_INST_KEY_UR_FRAG)){
                    movieArrayList = savedInstanceState.getParcelableArrayList(Constants.SAVED_INST_KEY_UR_FRAG);
                    state = sortBy;
                    Log.i(TAG,"Using user rated movies from saved instance");
                    return;
                }
            }else{
                if(savedInstanceState.containsKey(Constants.SAVED_INST_KEY_MP_FRAG)){
                    movieArrayList = savedInstanceState.getParcelableArrayList(Constants.SAVED_INST_KEY_MP_FRAG);
                    state = sortBy;
                    Log.i(TAG,"Using popular movies from saved instance");
                    return;
                }
            }
        }
        movieArrayList = new ArrayList<Movie>();
        state = null;
        Log.i(TAG,"Clean slate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "on createview");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieTilesAdapter = new MovieTilesAdapter(getActivity(), movieArrayList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_tiles);
        gridView.setColumnWidth(Constants.POSTER_WIDTH);
        gridView.setAdapter(movieTilesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = movieTilesAdapter.getItem(position);
                Intent detailIntent  = new Intent(getActivity(),DetailActivity.class);
                detailIntent.putExtra(Constants.SELECTED_MOVIE_INTENT,selectedMovie);
                startActivity(detailIntent);

            }
        });
        return rootView;
    }
    private  void updateTiles(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortBy = Integer.parseInt(settings.getString(getString(R.string.pref_sort_list_key),"1"));
        if(state == null ||state != sortBy){
            state = sortBy;
            new FetchMoviesTask().execute(sortBy);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"on start");
        updateTiles();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortBy = Integer.parseInt(settings.getString(getString(R.string.pref_sort_list_key), "1"));
        if(sortBy == 0){
            outState.putParcelableArrayList(Constants.SAVED_INST_KEY_UR_FRAG,movieArrayList);
        }else{
            outState.putParcelableArrayList(Constants.SAVED_INST_KEY_MP_FRAG,movieArrayList);
        }
        super.onSaveInstanceState(outState);
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, ArrayList<Movie>> {
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        protected ArrayList<Movie> doInBackground(Integer... params) {
            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String popularJsonStr = null;
            try{
                // Construct the URL for the TMDb query
                final String APIKEY = getResources().getString(R.string.TMDbAPIKEY);

                Uri uri = Uri.parse(Constants.API_BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", Constants.getSortStringPath(params[0]))
                        .appendQueryParameter("api_key", APIKEY)
                        .build();
                Log.i(TAG, uri.toString());
                URL url = new URL(uri.toString());
                // Create the request to TMDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                popularJsonStr = buffer.toString();
//                Log.i(TAG,popularJsonStr);


            } catch (java.io.IOException e) {
                Log.e(TAG, "Error ", e);
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                        e.printStackTrace();
                    }
                }
            }
            try {
                return getPopularMoviesFromJson(popularJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies != null){
                movieTilesAdapter.clear();
                movieArrayList = movies;
                for (Movie movie: movies){
                    movieTilesAdapter.add(movie);
                }
            }
        }


        private ArrayList<Movie> getPopularMoviesFromJson(String popularJsonStr) throws JSONException {

            final String RESULTS = "results";
            JSONObject receivedJson = new JSONObject(popularJsonStr);
            JSONArray receivedMoviesArray = receivedJson.getJSONArray(RESULTS);

            ArrayList<Movie> movies = new ArrayList<Movie>();

            for(int i = 0; i < receivedMoviesArray.length(); i++ ){
                JSONObject movieJson = receivedMoviesArray.getJSONObject(i);
                movies.add(i, new Movie(movieJson));
            }

            return movies;
        }
    }
}
