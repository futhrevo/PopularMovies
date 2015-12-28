package com.example.reku.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Hello");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Constants.SELECTED_MOVIE_INTENT)){
            Movie movie = intent.getParcelableExtra(Constants.SELECTED_MOVIE_INTENT);
            getActivity().setTitle(movie.original_title);
            TextView t = (TextView) rootView.findViewById(R.id.textView_original_title);
            t.setText(movie.original_title);

            ImageView v = (ImageView) rootView.findViewById(R.id.imageView_poster_detail);
            Picasso.with(getContext()).load(movie.getPoster_path()).into(v);

            t = (TextView) rootView.findViewById(R.id.textView_release_year);
            t.setText(movie.getYear());

            t = (TextView) rootView.findViewById(R.id.textView_vote_average);
            t.setText(movie.vote_average.toString());

            t = (TextView) rootView.findViewById(R.id.textView_overview);
            t.setText(movie.overview);
        }
        return rootView;
    }
}
