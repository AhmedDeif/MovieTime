package ahmedabodeif.movietime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

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



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static int i = 0;
    SharedPreferences mSharedPrefs;
    Context mCotext;
    boolean mTwoPane = false;
    String DETAILFRAGMENT_TAG = "DF";
    View rootView;
    private GridView movieGrid;
    private GridAdapter gridAdapter;
    private ArrayList<Movie> gridData = new ArrayList<Movie>();
    private ProgressBar mProgressBar;
    private String oldSetting;


    public MainActivityFragment() {
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Creates intent with required movie data attached.
    private  void startDetailActivity(int position){
        Intent intent = new Intent(this.getActivity(), MovieDetailActivity.class);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mCotext = this.getContext();
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCotext);
        oldSetting = mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular));
        //  Load Favourite movies from database.

        if(oldSetting.equals(getString(R.string.pref_sort_fav))) {

            DatabaseHandler db = new DatabaseHandler(this.getActivity());
            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
            gridData = new ArrayList<Movie>();
            gridData = db.getAll();
            // get data from db
            gridAdapter = new GridAdapter(mCotext, R.layout.movie_grid_item,gridData);
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

            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
            gridAdapter = new GridAdapter(mCotext, R.layout.movie_grid_item,gridData);
            movieGrid.setAdapter(gridAdapter);

            movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Start detail activity passing the movie information in intent.
                    startDetailActivity(position);
                }
            });
            FetChMoviesApiRequest api = new FetChMoviesApiRequest();
            api.execute();
            mProgressBar.setVisibility(View.VISIBLE);
        }
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }




    @Override
    public void onResume() {
        super.onResume();

        View rootView = this.getView();
        if(!(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular)).equals(oldSetting))) {

            if(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_label_popular))
                    .equals(getString(R.string.pref_sort_fav))) {

                DatabaseHandler db = new DatabaseHandler(this.getActivity());
                movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
                gridData = new ArrayList<Movie>();
                gridData = db.getAll();
                // get data from db
                gridAdapter = new GridAdapter(mCotext, R.layout.movie_grid_item,gridData);
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
                mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
                gridData = new ArrayList<Movie>();
                gridAdapter = new GridAdapter(mCotext, R.layout.movie_grid_item, gridData);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //  Detect Click on setting tab in action bar.
        if (id == R.id.action_settings) {
            startActivity(new Intent(this.getActivity(), SettingsActivity.class));
        }

        if(id == R.id.refresh) {
            movieGrid.invalidateViews();
            gridData = new ArrayList<Movie>();
            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
            gridAdapter = new GridAdapter(this.getActivity(), R.layout.movie_grid_item,gridData);
            movieGrid.setAdapter(gridAdapter);

            movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Start detail activity passing the movie information in intent.
                    startDetailActivity(position);
                }
            });
            mProgressBar.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }


    protected class FetChMoviesApiRequest extends AsyncTask<String,Void,Integer>{
        //  trailers link
        //  http://api.themoviedb.org/3/movie/122917/videos?api_key=005b0025bac9ea712583f4c6e318909b
        //  reviews link
        //  http://api.themoviedb.org/3/movie/122917/reviews?api_key=005b0025bac9ea712583f4c6e318909b
        final String baseURI = "http://api.themoviedb.org/3";
        final String discover = "discover/movie";
        final String movie = "movie";
        final String video = "videos";
        final String reviews = "reviews";
        final String SORT_PARAM = "sort_by";
        final String API_KEY = "api_key";
        final String apiKey = getString(R.string.api_key);

        private BufferedReader bufferedReader = null;
        private HttpURLConnection urlConnection = null;

        protected void getTrailers(String movieId){
            String response;
            Uri uri = Uri.parse(baseURI).buildUpon().
                    appendEncodedPath(movie).
                    appendEncodedPath(movieId).
                    appendEncodedPath(video).
                    appendQueryParameter(API_KEY,apiKey).build();
            URL url = null;
            try {
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                    //  stop
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line + "/n");
                }
                response = buffer.toString();

                // process for trailers
                processResponse(response);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void getReviews(String movieId){
            Uri uri = Uri.parse(baseURI).buildUpon().
                    appendEncodedPath(movie).
                    appendEncodedPath(movieId).
                    appendEncodedPath(reviews).
                    appendQueryParameter(API_KEY,apiKey).build();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 1;
            String sortParam = mSharedPrefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_label_popular));
            String response = null;
            Uri uri = Uri.parse(baseURI).buildUpon().
                    appendEncodedPath(discover).
                    appendQueryParameter(SORT_PARAM,sortParam).
                    appendQueryParameter(API_KEY,apiKey).build();
            URL url = null;
            try {
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                    return null;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line + "/n");
                }
                response = buffer.toString();
                processResponse(response);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                result = 0;
            }
            catch (IOException e) {
                e.printStackTrace();
                result = 0;
            } catch (JSONException e) {
                e.printStackTrace();
                result =  0;
            }

            result = 1;
            return result;
        }

        private void processResponse(String response) throws JSONException,IOException {

            JSONObject ob = new JSONObject(response);
            JSONArray results = ob.getJSONArray("results");
            Movie movie;
            for (int i=0; i<results.length();i++){
                JSONObject tempJSON = results.getJSONObject(i);
                movie = new Movie();
                movie.setTitle(tempJSON.getString("original_title"));
                movie.setPosterURL(tempJSON.getString("poster_path"));
                movie.setOverview(tempJSON.getString("overview"));
                movie.setRealseDate(tempJSON.getString("release_date"));
                movie.setRating(tempJSON.getString("vote_average"));
                movie.setMovieId(tempJSON.getString("id"));
                gridData.add(movie);
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                gridAdapter.setGridData(gridData);
            }
            mProgressBar.setVisibility(View.GONE);
        }

    }


}
