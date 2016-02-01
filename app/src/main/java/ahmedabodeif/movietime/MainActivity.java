package ahmedabodeif.movietime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    SharedPreferences mSharedPrefs;
    boolean mTwoPane = false;
    String DETAILFRAGMENT_TAG = "DF";
    private ArrayList<Movie> gridData = new ArrayList<Movie>();
    private String oldSetting;

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  used to detect change in settings so new data loaded on restart.
        oldSetting = mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_main);

        boolean tablet = isTablet(this.getBaseContext());
        if(findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane = false;
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


