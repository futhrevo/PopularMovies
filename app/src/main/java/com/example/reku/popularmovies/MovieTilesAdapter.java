package com.example.reku.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rakeshkalyankar on 26/12/15.
 */
public class MovieTilesAdapter extends ArrayAdapter<Movie>{
    private final String TAG = MovieTilesAdapter.class.getSimpleName();

    public MovieTilesAdapter(Context context, List<Movie> movieList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the Movie object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_poster,parent,false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.list_item_thumb);
        Picasso.with(getContext()).load(movie.getPoster_path()).placeholder(R.mipmap.ic_launcher).into(posterView);

        return convertView;
    }
}
