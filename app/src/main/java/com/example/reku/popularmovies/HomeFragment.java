package com.example.reku.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.reku.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String TAG = HomeFragment.class.getSimpleName();

    private MoviesCursorAdaptor moviesCursorAdaptor;
    private static final int CURSOR_LOADER_ID = 0;
    public HomeFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if(Utility.getPrefSelected(getActivity()) == Constants.PREF_FAVORITE){
            Log.i(TAG, "favorite selected");
            item.setIcon(R.drawable.ic_favorite_black_24dp);
        }else{
            item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            Log.i(TAG, "Not a favorite");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            if(Utility.getPrefSelected(getActivity()) == Constants.PREF_FAVORITE){
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                Utility.putPrefSelected(getActivity(), 1);
            }else{
                item.setIcon(R.drawable.ic_favorite_black_24dp);
                Utility.putPrefSelected(getActivity(), Constants.PREF_FAVORITE);
            }
            onPreferenceChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                if (cursor != null) {
                    Long idLong = cursor.getLong(cursor.getColumnIndex("_id"));
                    Log.i(TAG, idLong.toString());
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    detailIntent.setData(MovieContract.MovieEntry.buildMovieUri(idLong));
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



    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    void onPreferenceChanged(){
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
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
        Log.i(TAG, "cursor load finished");
        moviesCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdaptor.swapCursor(null);
    }
}
