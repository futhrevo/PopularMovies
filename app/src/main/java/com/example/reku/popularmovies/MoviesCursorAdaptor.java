package com.example.reku.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by rakeshkalyankar on 12/02/16.
 */
public class MoviesCursorAdaptor extends CursorAdapter {

    private static final String TAG = MoviesCursorAdaptor.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public MoviesCursorAdaptor(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_poster,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Movie movie = new Movie(cursor);
        viewHolder.posterView.setContentDescription(movie.original_title);
        Picasso.with(mContext).load(movie.getPoster_path()).placeholder(R.mipmap.ic_launcher).error(R.drawable.ic_cloud_off_black_36px).into(viewHolder.posterView);
    }

    public static class ViewHolder{
        public final ImageView posterView;

        public ViewHolder(View view){
            posterView = (ImageView) view.findViewById(R.id.list_item_thumb);
        }

    }
}
