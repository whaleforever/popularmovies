package com.steward.whale.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by whale on 16/05/17.
 */

public class ImageAdapter extends BaseAdapter {

    final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<String> imageUrls;

    public ImageAdapter(Context c){
        mContext = c;
    }

    public int getCount(){

        if (imageUrls != null){
            return imageUrls.size();
        }
        return 0;
    }

    public void setUrls(ArrayList<String> urls ){ imageUrls = urls;}

    public long getItemId(int position){return position;}

    public String getItem(int position) {
        return imageUrls.get(position);
    }

    public View getView (int position, View convertView, ViewGroup parent ){
        ImageView imageView;

        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(8,8,8,8);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = getItem(position);
        Log.d(LOG_TAG, url);
        Picasso.with(mContext).load("https://image.tmdb.org/t/p/w185/"+url).into(imageView);
        return imageView;
    }
}
