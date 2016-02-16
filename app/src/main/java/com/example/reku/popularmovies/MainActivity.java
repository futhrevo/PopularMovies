package com.example.reku.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.reku.popularmovies.data.MovieContract;
import com.facebook.stetho.Stetho;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private int mLastPreference;
    private final String HOMEFRAGMENT_TAG = "HMTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLastPreference = Utility.getPrefSelected(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null){
            pushFragments(new HomeFragment(), false, HOMEFRAGMENT_TAG);
        }
        updateTiles();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

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

    @Override
    protected void onResume() {
        super.onResume();
        int pref = Utility.getPrefSelected(this);
        if(pref != mLastPreference){
            HomeFragment hm = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOMEFRAGMENT_TAG);
            if(hm != null){
                hm.onPreferenceChanged();
            }
            mLastPreference = pref;
        }
    }

    public void pushFragments(Fragment fragment, boolean shouldAdd, String tag) {
        FragmentManager manager = getSupportFragmentManager();//Use this line for FragmentManager , if you are in Activity
        //FragmentManager manager = getActivity().getSupportFragmentManager();//Use this line for FragmentManager , if you are in Fragment
        FragmentTransaction ft = manager.beginTransaction();

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//Uncomment this line if you don't want to maintain Fragment Backsack
        ft.replace(R.id.content_main, fragment, tag);
        if (shouldAdd) {
            ft.addToBackStack(tag); //add fragment in backstack
        }
        ft.commit();
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
}
