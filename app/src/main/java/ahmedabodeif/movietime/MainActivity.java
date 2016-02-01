package ahmedabodeif.movietime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView movieGrid;
    private GridAdapter gridAdapter;
    private ArrayList<Movie> gridData = new ArrayList<Movie>();
    private ProgressBar mProgressBar;
    private String oldSetting;
    SharedPreferences mSharedPrefs ;
    static int i =0;
    boolean mTwoPane = false;
    String DETAILFRAGMENT_TAG = "DF";


    @Override
    protected void onStop() {
        super.onStop();
        //  used to detect change in settings so new data loaded on restart.
        oldSetting = mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular));
        Log.e("onStop","now " + i++);
    }

    /*
    @Override
    protected void onRestart() {
        super.onRestart();

        if(!(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular)).equals(oldSetting))) {

            if(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_label_popular))
                    .equals(getString(R.string.pref_sort_fav))) {

                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                movieGrid = (GridView) findViewById(R.id.movieGrid);
                gridData = new ArrayList<Movie>();
                gridData = db.getAll();
                // get data from db
                gridAdapter = new GridAdapter(this, R.layout.movie_grid_item,gridData);
                movieGrid.setAdapter(gridAdapter);
                mProgressBar.setVisibility(View.INVISIBLE);
                movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Start detail activity passing the movie information in intent.
                        startDetailActivity(position);
                    }
                });
            }
            else {

                // Since setting changed must update grid with new data.
                movieGrid.invalidateViews();
                mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
                movieGrid = (GridView) findViewById(R.id.movieGrid);
                gridData = new ArrayList<Movie>();
                gridAdapter = new GridAdapter(this, R.layout.movie_grid_item, gridData);
                movieGrid.setAdapter(gridAdapter);
                mProgressBar.setVisibility(View.VISIBLE);

                movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Start detail activity passing the movie information in intent.
                        startDetailActivity(position);
                    }
                });
                MainActivityFragment.FetChMoviesApiRequest api = new MainActivityFragment.FetChMoviesApiRequest();
                api.execute();
            }
        }

    }*/

    @Override
    protected void onResume() {
        super.onResume();

    }

    public boolean checkScreenSize(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_main);

        boolean tablet = isTablet(this.getBaseContext());
        //tablet = checkScreenSize(this.getBaseContext());
        if(tablet)
            Log.e("App on Tablet ", "True");
        else
            Log.e("App on Tablet ", "False");
        if(findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            Log.e("its a two pane bt3","Oh yeah");
            if (savedInstanceState == null) {
                                getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.movie_detail_container, new MovieDetailActivityFragment(), DETAILFRAGMENT_TAG)
                                                .commit();
                            }
        }
        else{
            mTwoPane = false;
            Log.e("its a phone, two P","Works");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        oldSetting = mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular));
        //  Load Favourite movies from database.

        if(oldSetting.equals(getString(R.string.pref_sort_fav))) {

            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            movieGrid = (GridView) findViewById(R.id.movieGrid);
            gridData = new ArrayList<Movie>();
            gridData = db.getAll();
            // get data from db
            gridAdapter = new GridAdapter(this, R.layout.movie_grid_item,gridData);
            movieGrid.setAdapter(gridAdapter);
            mProgressBar.setVisibility(View.INVISIBLE);
            movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Start detail activity passing the movie information in intent.
                    startDetailActivity(position);
                }
            });
        }
        //  Data fetched from movidb API.
        else {

            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            movieGrid = (GridView) findViewById(R.id.movieGrid);
            gridAdapter = new GridAdapter(this, R.layout.movie_grid_item,gridData);
            movieGrid.setAdapter(gridAdapter);

            movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Start detail activity passing the movie information in intent.
                    startDetailActivity(position);
                }
            });
            apiRequest api = new apiRequest();
            api.execute();
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_main);

        boolean tablet = isTablet(this.getBaseContext());
        //tablet = checkScreenSize(this.getBaseContext());
        if(tablet)
            Log.e("App on Tablet ", "True");
        else
            Log.e("App on Tablet ", "False");
        if(findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            Log.e("its a two pane bt3","Oh yeah");
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane = false;
            Log.e("its a phone, two P","Works");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // Creates intent with required movie data attached.
    private  void startDetailActivity(int position){
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie",position);
        Movie tmp = (Movie) gridData.get(position);
        intent.putExtra("movieTitle",tmp.getTitle());
        intent.putExtra("movieRating",tmp.getRating());
        intent.putExtra("moviePoster",tmp.getPosterURL());
        intent.putExtra("movieDate",tmp.getRealseDate());
        intent.putExtra("movieLength",tmp.getOverview());
        intent.putExtra("description",tmp.getOverview());
        intent.putExtra("id",tmp.getMovieId());
        if(tmp._image == null)
            tmp.setMoviePoster(tmp.getMoviePoster());
        intent.putExtra("image",tmp._image);
        startActivity(intent);
    }




}


