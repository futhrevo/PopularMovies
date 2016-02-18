package com.example.reku.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private static final String SELECTED_KEY = "selectedKey";
    Callback mCallback;
    private int mPosition;
    private GridView gridView;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;
        if(context instanceof Activity){
            a= (Activity) context;
            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try{
                mCallback = (Callback) a;
            }catch (ClassCastException e){
                throw new ClassCastException(a.toString()
                        + " must implement Callback interface");

            }

        }
    }


    public interface Callback{
        void onItemSelected(Uri movieUri);
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
        gridView = (GridView) rootView.findViewById(R.id.gridView_tiles);
        gridView.setColumnWidth(Constants.POSTER_WIDTH);
        gridView.setAdapter(moviesCursorAdaptor);
        gridView.setEmptyView(rootView.findViewById(R.id.imageView_empty_grid));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null && cursor.moveToPosition(position)) {
                    Long idLong = cursor.getLong(cursor.getColumnIndex("_id"));
                    Log.i(TAG, idLong.toString());

                    Uri destUri = MovieContract.MovieEntry.buildMovieUri(idLong);
                    mCallback.onItemSelected(destUri);
                }
                mPosition = position;
            }
        });
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
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
        onPreferenceChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
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
        if (mPosition != GridView.INVALID_POSITION){
            gridView.smoothScrollToPosition(mPosition);
        }
        long defaultId = moviesCursorAdaptor.getItemId(0);
        Log.i(TAG, "default Id is " + defaultId);

        if(data.moveToFirst()){
            new Handler().post(new Runnable() {
                public void run() {
                    gridView.performItemClick(gridView.getAdapter().getView(0, null, null), 0, gridView.getAdapter().getItemId(0));
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdaptor.swapCursor(null);
    }
}
