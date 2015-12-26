package com.example.reku.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieTilesAdapter = new MovieTilesAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_tiles);
        gridView.setColumnWidth(Constants.POSTER_WIDTH);
        gridView.setAdapter(movieTilesAdapter);
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_tiles);
//        listView.setAdapter(movieTilesAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG, "onstart called");
        new FetchMoviesTask().execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        protected Movie[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String popularJsonStr = null;
            try{
                // Construct the URL for the TMDb query
                final String APIKEY = getResources().getString(R.string.TMDbAPIKEY);

                Uri uri = Uri.parse(Constants.API_BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("api_key", APIKEY)
                        .build();
//                Log.i(TAG, uri.toString());
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
        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                movieTilesAdapter.clear();
                for (Movie movie: movies){
                    movieTilesAdapter.add(movie);
                }
            }
        }


        private Movie[] getPopularMoviesFromJson(String popularJsonStr) throws JSONException {

            final String RESULTS = "results";
            JSONObject receivedJson = new JSONObject(popularJsonStr);
            JSONArray receivedMoviesArray = receivedJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[receivedMoviesArray.length()];

            for(int i = 0; i < receivedMoviesArray.length(); i++ ){
                JSONObject movieJson = receivedMoviesArray.getJSONObject(i);
                movies[i] = new Movie(movieJson);
            }

            return movies;
        }
    }
}
