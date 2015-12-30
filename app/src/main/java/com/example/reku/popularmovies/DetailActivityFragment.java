package com.example.reku.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    @Bind(R.id.textView_original_title) TextView textView_original_title;
    @Bind(R.id.imageView_poster_detail) ImageView imageView_poster_detail;
    @Bind(R.id.textView_release_year) TextView textView_release_year;
    @Bind(R.id.textView_vote_average) TextView textView_vote_average;
    @Bind(R.id.textView_overview) TextView textView_overview;
    @Bind(R.id.textView_runtime) TextView textView_runtime;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Hello");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Constants.SELECTED_MOVIE_INTENT)){
            Movie movie = intent.getParcelableExtra(Constants.SELECTED_MOVIE_INTENT);
            getActivity().setTitle(movie.original_title);

            textView_original_title.setText(movie.original_title);
            Picasso.with(getContext()).load(movie.getPoster_path()).into(imageView_poster_detail);
            textView_release_year.setText(movie.getYear());
            textView_vote_average.setText(movie.vote_average.toString() + "/10");
            textView_overview.setText(movie.overview);

            // fetch meta data from async task
            new FetchMovieMetaTask().execute(movie.id);
        }
        return rootView;
    }

    public class FetchMovieMetaTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonstr = null;
            try{
                final String APIKEY = getResources().getString(R.string.TMDbAPIKEY);
                Uri uri = Uri.parse(Constants.META_BASE_URL + params[0]).buildUpon()
                        .appendQueryParameter("api_key",APIKEY)
                        .build();

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
            if(s != null){
                try {
                    JSONObject metaJson = new JSONObject(s);
                    String runtime = metaJson.getString("runtime");

                    // if view is destroyed before network operation is done
                    if(textView_runtime != null){
                        textView_runtime.setText(runtime + "min");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
