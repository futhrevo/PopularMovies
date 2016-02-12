package com.example.reku.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String TAG =  DetailActivityFragment.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;

    @Bind(R.id.imageView_poster_detail) ImageView imageView_poster_detail;
    @Bind(R.id.textView_release_year) TextView textView_release_year;
    @Bind(R.id.textView_vote_average) TextView textView_vote_average;
    @Bind(R.id.textView_overview) TextView textView_overview;
    @Bind(R.id.textView_runtime) TextView textView_runtime;
    @Bind(R.id.listView_trailers) LinearListView listView_trailers;
    @Bind(R.id.linearlist) LinearListView linearListView;
    @Bind(R.id.progressBar) ProgressBar progressBar;



    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        //add fab functionality
        final FloatingActionButton myFab = (FloatingActionButton) getActivity().findViewById(R.id.detail_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFab.setSelected(!myFab.isSelected());
                myFab.setImageResource(myFab.isSelected() ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
//                myFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                Log.i(TAG, " fab clicked");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);


        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Constants.SELECTED_MOVIE_INTENT)){
            final Movie movie = intent.getParcelableExtra(Constants.SELECTED_MOVIE_INTENT);
            getActivity().setTitle(movie.original_title);
//            textView_original_title.setText(movie.original_title);
            Picasso.with(getContext()).load(movie.getPoster_path()).into(imageView_poster_detail);
            textView_release_year.setText(movie.original_title + " \n\n" + movie.getYear());
            textView_vote_average.setText(movie.vote_average.toString() + "/10");
            textView_overview.setText(movie.overview);


            // fetch meta data from async task
            new TmdbApiTask(getActivity(), textView_runtime).execute(movie.id);

            //fetch trailer list
            new TmdbApiTask(getActivity(), listView_trailers, Constants.FETCH_VIDEOS, new TmdbApiTask.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    if(mShareActionProvider != null){
                        Intent shareIntent =  createShareVideoIntent(result);
                        mShareActionProvider.setShareIntent(shareIntent);
                    }
                }
            }).execute(movie.id);

            //fetch reviews list
            new TmdbApiTask(getActivity(), linearListView, Constants.FETCH_REVIEWS, new TmdbApiTask.AsyncResponse() {
                @Override
                public void processFinish(String str) {
                    progressBar.setVisibility(View.GONE);
                    linearListView.setEmptyView(rootView.findViewById(R.id.textView_empty_list_item));
                }
            }).execute(movie.id);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareVideoIntent(null));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareVideoIntent(String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String link;
        if(key != null){
           link =  "Checkout " + getActivity().getTitle() + " movie trailer @ " + "http://www.youtube.com/watch?v="+ key +" ";
        } else{
            link =  "Checkout " + getActivity().getTitle() + " movie ";
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                link + Constants.MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }


}
