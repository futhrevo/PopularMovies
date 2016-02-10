package com.example.reku.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
//    @Bind(R.id.textView_original_title) TextView textView_original_title;
    @Bind(R.id.imageView_poster_detail) ImageView imageView_poster_detail;
    @Bind(R.id.textView_release_year) TextView textView_release_year;
    @Bind(R.id.textView_vote_average) TextView textView_vote_average;
    @Bind(R.id.textView_overview) TextView textView_overview;
    @Bind(R.id.textView_runtime) TextView textView_runtime;
    @Bind(R.id.listView_trailers) ListView listView_trailers;
    @Bind(R.id.listView_reviews) ListView listView_reviews;
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

//            textView_original_title.setText(movie.original_title);
            Picasso.with(getContext()).load(movie.getPoster_path()).into(imageView_poster_detail);
            textView_release_year.setText(movie.getYear());
            textView_vote_average.setText(movie.vote_average.toString() + "/10");
            textView_overview.setText(movie.overview);

            // fetch meta data from async task
            new TmdbApiTask(getActivity(),textView_runtime).execute(movie.id);

            //fetch trailer list
            new TmdbApiTask(getActivity(), listView_trailers, Constants.FETCH_VIDEOS).execute(movie.id);

            //fetch reviews list
            new TmdbApiTask(getActivity(), listView_reviews, Constants.FETCH_REVIEWS).execute(movie.id);
        }
        return rootView;
    }
}
