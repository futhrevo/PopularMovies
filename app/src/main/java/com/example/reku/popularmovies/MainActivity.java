package com.example.reku.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.reku.popularmovies.data.MovieContract;
import com.facebook.stetho.Stetho;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements HomeFragment.Callback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private int mLastPreference;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLastPreference = Utility.getPrefSelected(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateTiles();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

        if(findViewById(R.id.movie_detail_container) != null){
            Log.i(TAG, "Two Pane Layout");
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void updateTiles(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long bestBefore = settings.getLong(Constants.VALID_TILL, 0L);

        if(new Date().after(new Date(bestBefore))){
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
            new TmdbApiTask(getApplicationContext()).execute(String.valueOf(Constants.PREF_HIGH_RATED));
            new TmdbApiTask(getApplicationContext()).execute(String.valueOf(Constants.PREF_MOST_POPULAR));
            // refresh data every day
            settings.edit().putLong(Constants.VALID_TILL, new Date().getTime() + 86400000L).apply();

        } else{
            return;
        }
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        Log.i(TAG,movieUri.toString());
        DetailActivityFragment daf = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if(daf != null){
            // If detail activity frag is available, we're in two-pane layout...
            Bundle args = new Bundle();
            args.putParcelable(Constants.DETAILFRAG_KEY, movieUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }else{
            // Otherwise, we're in the one-pane layout...

            Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
            detailIntent.setData(movieUri);
            startActivity(detailIntent);
        }
    }
}
