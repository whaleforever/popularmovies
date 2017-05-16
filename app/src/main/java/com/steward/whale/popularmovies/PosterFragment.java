package com.steward.whale.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PosterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private ImageAdapter mMovieAdapter;
    private String[] imageUrls;

    public PosterFragment() {
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovie();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);

        mMovieAdapter = new ImageAdapter(getActivity());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }

    public void updateMovie(){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        movieTask.execute();
    }



    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private ArrayList<String> getMovieDataFromJson(String moviesJsonStr ) throws JSONException{
            ArrayList<String> resultsStrs = new ArrayList<String>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            for (int i =0; i < moviesArray.length(); i++){
                JSONObject movie = moviesArray.getJSONObject(i);
                resultsStrs.add(movie.getString("poster_path"));
            }
            return resultsStrs;
        }

        protected ArrayList<String> doInBackground(String... params){
            Log.d(LOG_TAG, "open background");

            // safe guard
//            if (params.length == 0){
//                return null;
//            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // raw json
            String moviesJsonStr = null;

            Log.d(LOG_TAG,"calling connecting");
            try {
                Uri uri = Uri.parse("https://api.themoviedb.org/3/movie/popular")
                        .buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                        .appendQueryParameter("language", "en-us")
                        .appendQueryParameter("page", "1")
                        .build();

                Log.d(LOG_TAG, uri.toString());
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                // assume nothing to fetch
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // add new line to make debugging easier
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                // when stream was empty
                if (buffer.length() == 0){
                    return null;
                }

                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG, moviesJsonStr);

            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally{
                // close the connection
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error", e);
                    }

                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results){
            if (results != null){
                mMovieAdapter.setUrls(results);
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }
}
