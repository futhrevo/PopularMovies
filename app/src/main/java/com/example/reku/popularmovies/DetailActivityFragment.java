package com.example.reku.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.reku.popularmovies.data.MovieContract.MovieEntry;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG =  DetailActivityFragment.class.getSimpleName();
    private Uri mUri;

    private ShareActionProvider mShareActionProvider;

    @Bind(R.id.imageView_poster_detail) ImageView imageView_poster_detail;
    @Bind(R.id.textView_release_year) TextView textView_release_year;
    @Bind(R.id.textView_vote_average) TextView textView_vote_average;
    @Bind(R.id.textView_overview) TextView textView_overview;
    @Bind(R.id.textView_runtime) TextView textView_runtime;
    @Bind(R.id.listView_trailers) LinearListView listView_trailers;
    @Bind(R.id.linearlist) LinearListView linearListView;
    @Bind(R.id.progressBar) ProgressBar progressBar;

    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS =  {
            MovieEntry.TABLE_NAME +"."+ MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER,
            MovieEntry.COLUMN_USER_RATING,
            MovieEntry.COLUMN_SYNOPSIS,
            MovieEntry.COLUMN_FAVORITE
    };
    private FloatingActionButton myFab;

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_DATE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    public static final int COL_MOVIE_RATING = 4;
    public static final int COL_MOVIE_SYNOPSIS = 5;
    public static final int COL_MOVIE_FAV = 6;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        //        //add fab functionality
        myFab = (FloatingActionButton) getActivity().findViewById(R.id.detail_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                if(myFab.isSelected()){
                    cv.put(MovieEntry.COLUMN_FAVORITE, false);
                    Log.i(TAG, " fab clicked");
                }else {
                    cv.put(MovieEntry.COLUMN_FAVORITE, true);
                }
//                myFab.setSelected(!myFab.isSelected());
//                myFab.setImageResource(myFab.isSelected() ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp);
                if(cv.size() > 0){
                    int updated = getContext().getContentResolver().update(mUri,cv,null,null);
                    getLoaderManager().restartLoader(DETAIL_LOADER, null, DetailActivityFragment.this);
                }

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        if(intent != null){
            mUri = intent.getData();
            long movieId = ContentUris.parseId(mUri);
            // fetch meta data from async task
            new TmdbApiTask(getActivity(), textView_runtime).execute(String.valueOf(movieId));

            //fetch trailer list
            new TmdbApiTask(getActivity(), listView_trailers, Constants.FETCH_VIDEOS, new TmdbApiTask.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    listView_trailers.setEmptyView(rootView.findViewById(R.id.textView_empty_trailer_list));
                    if(mShareActionProvider != null){
                        Intent shareIntent =  createShareVideoIntent(result);
                        mShareActionProvider.setShareIntent(shareIntent);
                    }
                }
            }).execute(String.valueOf(movieId));

            //fetch reviews list
            new TmdbApiTask(getActivity(), linearListView, Constants.FETCH_REVIEWS, new TmdbApiTask.AsyncResponse() {
                @Override
                public void processFinish(String str) {
                    progressBar.setVisibility(View.GONE);
                    linearListView.setEmptyView(rootView.findViewById(R.id.textView_empty_list_item));
                }
            }).execute(String.valueOf(movieId));
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri != null){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(COL_MOVIE_TITLE);
            getActivity().setTitle(title);
            String year = data.getString(COL_MOVIE_DATE);

            textView_release_year.setText(title + " \n\n" + Utility.getYear(year));

            String overview = data.getString(COL_MOVIE_SYNOPSIS);
            textView_overview.setText(overview);

            String rating = data.getString(COL_MOVIE_RATING);
             textView_vote_average.setText(rating + "/10");

            String poster = data.getString(COL_MOVIE_POSTER);

        Picasso.with(getContext())
                .load(Utility.getPoster_path(Constants.DEFAULT_POSTER_WIDTH, poster))
                .into(imageView_poster_detail);

            Boolean fav = data.getInt(COL_MOVIE_FAV) > 0;
            if(fav){
                myFab.setSelected(true);
                myFab.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else{
                myFab.setSelected(false);
                myFab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
