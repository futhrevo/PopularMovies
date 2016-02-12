package com.example.reku.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.reku.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String TAG = HomeFragment.class.getSimpleName();

    private MoviesCursorAdaptor moviesCursorAdaptor;
    private ArrayList<Movie> movieArrayList;
    private Integer state ;
    private static final int CURSOR_LOADER_ID = 0;
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
//                    Log.i(TAG,"Using user rated movies from saved instance");
                    return;
                }
            }else{
                if(savedInstanceState.containsKey(Constants.SAVED_INST_KEY_MP_FRAG)){
                    movieArrayList = savedInstanceState.getParcelableArrayList(Constants.SAVED_INST_KEY_MP_FRAG);
                    state = sortBy;
//                    Log.i(TAG,"Using popular movies from saved instance");
                    return;
                }
            }
        }
        movieArrayList = new ArrayList<>();
        state = null;
//        Log.i(TAG,"Clean slate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesCursorAdaptor = new MoviesCursorAdaptor(getActivity(), null, 0, CURSOR_LOADER_ID);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_tiles);
        gridView.setColumnWidth(Constants.POSTER_WIDTH);
        gridView.setAdapter(moviesCursorAdaptor);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    Movie selectedMovie = new Movie(cursor);
                    Intent detailIntent  = new Intent(getActivity(),DetailActivity.class);
                    detailIntent.putExtra(Constants.SELECTED_MOVIE_INTENT,selectedMovie);
                    startActivity(detailIntent);
                }


            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private  void updateTiles(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortBy = Integer.parseInt(settings.getString(getString(R.string.pref_sort_list_key),"1"));
        if(state == null ||state != sortBy){
            state = sortBy;
//            new FetchMoviesTask().execute(sortBy);
//            new TmdbApiTask(getActivity(),movieArrayList).execute(String.valueOf(sortBy));
        }

    }
    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG,"on start");
        updateTiles();
    }

    void onPreferenceChanged(){
        getLoaderManager().restartLoader(CURSOR_LOADER_ID,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        int sortBy = Utility.getPrefSelected(getActivity());
        if(sortBy == 0){
            outState.putParcelableArrayList(Constants.SAVED_INST_KEY_UR_FRAG,movieArrayList);
        }else{
            outState.putParcelableArrayList(Constants.SAVED_INST_KEY_MP_FRAG,movieArrayList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Read preferences for obtaining sort order and favorites

        int sortBy = Utility.getPrefSelected(getActivity());
        String sortOrder;
        String selection = null;
        switch (sortBy){
            case Constants.PREF_HIGH_RATED:
                sortOrder = MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC";
                break;
            case Constants.PREF_FAVORITE:
                selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_FAVORITE + " = 1 ";
            case Constants.PREF_MOST_POPULAR:
                sortOrder = MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DESC";
                break;
            default:
                sortOrder = null;

        }
        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, null,selection,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdaptor.swapCursor(null);
    }
}
