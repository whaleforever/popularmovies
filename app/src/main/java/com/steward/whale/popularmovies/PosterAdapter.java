package com.steward.whale.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by whale on 5/19/17.
 */

public class PosterAdapter extends ArrayAdapter<Poster> {
    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    public PosterAdapter(Activity context, ArrayList<Poster> movies){
        super (context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ){
        Poster poster = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.grid_item_poster_gridview);
        Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w185/" + poster.posterUrl).into(image);
        return convertView;
    }
}
