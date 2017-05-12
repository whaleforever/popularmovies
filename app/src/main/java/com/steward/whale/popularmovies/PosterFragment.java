package com.steward.whale.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PosterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PosterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PosterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PosterFragment newInstance(String param1, String param2) {
        PosterFragment fragment = new PosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // temporary stuff
        Log.d("begin", "calling api");
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poster, container, false);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected String[] doInBackground(String... params){
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

            return null;
        }
    }
}
