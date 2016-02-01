package ahmedabodeif.movietime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
public class MovieDetailActivityFragment extends Fragment {

    ListView mTrailerList;
    TrailerAdapter mTrailerAdap;
    ArrayList<Trailer> trailerList = new ArrayList<Trailer>();
    ArrayList<MovieReview> reviewsList = new ArrayList<MovieReview>();
    //String[] params = {"hi"};
    SharedPreferences mSharedPrefs;
    String MOVIE_SHARE_HASHTAG = "\t #MovieTime";
    String mMovieString;
    android.support.v7.widget.ShareActionProvider mShareActionProvider;
    Context mContext;
    PackageManager mPackageManger;
    Activity mActivity;



    public MovieDetailActivityFragment() {
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

    public View phoneCreateView(View rootView){

        final Intent intent = this.getActivity().getIntent();
        // setting movie title
        TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        movieTitle.setText("\t"+intent.getStringExtra("movieTitle"));
        // setting movie rating
        TextView movieRating = (TextView) rootView.findViewById(R.id.ratingText);
        movieRating.setText(intent.getStringExtra("movieRating"));
        // setting imageView

        //ImageView image = (ImageView) findViewById(R.id.imageView);
        //Picasso.with(this.getBaseContext()).load(intent.getStringExtra("moviePoster")).into(image);
        // setting movie date
        TextView movieDate = (TextView) rootView.findViewById(R.id.movieProductionYear);
        movieDate.setText(intent.getStringExtra("movieDate"));
        //setting movie description
        TextView movieDescription = (TextView) rootView.findViewById(R.id.movieDescription);
        movieDescription.setText(intent.getStringExtra("description"));


        Button bt = (Button) rootView.findViewById(R.id.favouriteButton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  insert into db
                Movie tmp = new Movie();
                tmp.setMovieId(intent.getStringExtra("id"));
                tmp.setOverview(intent.getStringExtra("description"));
                tmp.setRealseDate(intent.getStringExtra("movieDate"));
                tmp.setRating(intent.getStringExtra("movieRating"));
                tmp.setTitle(intent.getStringExtra("movieTitle"));
                tmp._image = intent.getByteArrayExtra("image");
                // try creating a bitmap here to see the byte arry from intent
                ByteArrayInputStream imageStream = new ByteArrayInputStream(tmp._image);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                DatabaseHandler db = new DatabaseHandler(mContext);
                db.addMovie(tmp);
                Log.e("Add to DB" , "Successful");
            }
        });
        String[] params = {"hi"};
        if(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular)).
                equals(getString(R.string.pref_sort_fav))){
            ByteArrayInputStream imageStream = new ByteArrayInputStream(intent.getByteArrayExtra("image"));
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
            image.setImageBitmap(theImage);
            params[0] = intent.getStringExtra("id");
            GetMovieDetailsApiRequest task = (GetMovieDetailsApiRequest) new GetMovieDetailsApiRequest().execute(params);
        }
        else {
            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
            Picasso.with(mContext).load(intent.getStringExtra("moviePoster")).into(image);
            // tablet i get empty intent so params null so everything breaks
            params[0] = intent.getStringExtra("id");
            GetMovieDetailsApiRequest task = (GetMovieDetailsApiRequest) new GetMovieDetailsApiRequest().execute(params);
        }
        return rootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mActivity = this.getActivity();
        mContext = this.getContext();
        mPackageManger = this.getActivity().getPackageManager();
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        if(!isTablet(mContext))
            rootView = phoneCreateView(rootView);

        //  if tablet

        /*
        final Intent intent = this.getActivity().getIntent();
        // setting movie title
        TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        movieTitle.setText("\t"+intent.getStringExtra("movieTitle"));
        // setting movie rating
        TextView movieRating = (TextView) rootView.findViewById(R.id.ratingText);
        movieRating.setText(intent.getStringExtra("movieRating"));
        // setting imageView

        //ImageView image = (ImageView) findViewById(R.id.imageView);
        //Picasso.with(this.getBaseContext()).load(intent.getStringExtra("moviePoster")).into(image);
        // setting movie date
        TextView movieDate = (TextView) rootView.findViewById(R.id.movieProductionYear);
        movieDate.setText(intent.getStringExtra("movieDate"));
        //setting movie description
        TextView movieDescription = (TextView) rootView.findViewById(R.id.movieDescription);
        movieDescription.setText(intent.getStringExtra("description"));


        Button bt = (Button) rootView.findViewById(R.id.favouriteButton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  insert into db
                Movie tmp = new Movie();
                tmp.setMovieId(intent.getStringExtra("id"));
                tmp.setOverview(intent.getStringExtra("description"));
                tmp.setRealseDate(intent.getStringExtra("movieDate"));
                tmp.setRating(intent.getStringExtra("movieRating"));
                tmp.setTitle(intent.getStringExtra("movieTitle"));
                tmp._image = intent.getByteArrayExtra("image");
                // try creating a bitmap here to see the byte arry from intent
                ByteArrayInputStream imageStream = new ByteArrayInputStream(tmp._image);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                DatabaseHandler db = new DatabaseHandler(mContext);
                db.addMovie(tmp);
                Log.e("Add to DB" , "Successful");
            }
        });


        String[] params = {"hi"};
        if(mSharedPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular)).
                equals(getString(R.string.pref_sort_fav))){
            ByteArrayInputStream imageStream = new ByteArrayInputStream(intent.getByteArrayExtra("image"));
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
            image.setImageBitmap(theImage);
            params[0] = intent.getStringExtra("id");
            GetMovieDetailsApiRequest task = (GetMovieDetailsApiRequest) new GetMovieDetailsApiRequest().execute(params);
        }
        else {
            ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
            Picasso.with(mContext).load(intent.getStringExtra("moviePoster")).into(image);
            // tablet i get empty intent so params null so everything breaks
            params[0] = intent.getStringExtra("id");
            GetMovieDetailsApiRequest task = (GetMovieDetailsApiRequest) new GetMovieDetailsApiRequest().execute(params);
        }*/

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //mShareActionProvider.setShareIntent(createShareForecastIntent());
        Log.e("share provider","intialised");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this.getActivity(), SettingsActivity.class));
        }
        if (id == R.id.action_share && PreferenceManager.
                getDefaultSharedPreferences(
                        this.getContext()).getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_label_popular)).
                equals(getString(R.string.pref_sort_fav))){

        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieString + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }


    private class GetMovieDetailsApiRequest extends AsyncTask<String,Void,Void> {

        //  trailers link
        //  http://api.themoviedb.org/3/movie/122917/videos?api_key=005b0025bac9ea712583f4c6e318909b
        //  reviews link
        //  http://api.themoviedb.org/3/movie/122917/reviews?api_key=005b0025bac9ea712583f4c6e318909b
        final String baseURI = "http://api.themoviedb.org/3";
        final String movie = "movie";
        final String video = "videos";
        final String reviews = "reviews";
        final String API_KEY = "api_key";
        final String apiKey = getString(R.string.api_key);
        String movieID = "";

        String LOG_TAG = GetMovieDetailsApiRequest.class.getSimpleName();
        private BufferedReader bufferedReader = null;
        private HttpURLConnection urlConnection = null;

        private void getMovieReviews(String movieID){
            String response;

            Uri uri = Uri.parse(baseURI).buildUpon().
                    appendEncodedPath(movie).
                    appendEncodedPath(movieID).
                    appendEncodedPath(reviews).
                    appendQueryParameter(API_KEY,apiKey).
                    build();
            Log.e("gettin reviews",uri.toString());
            URL url = null;
            try {
                Log.e("url",uri.toString());
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                    return;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line + "/n");
                }
                response = buffer.toString();

                // process for trailers
                processResponseReviews(response);
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

        protected void getTrailers(String movieId){
            String response;
            Uri uri = Uri.parse(baseURI).buildUpon().
                    appendEncodedPath(movie).
                    appendEncodedPath(movieId).
                    appendEncodedPath(video).
                    appendQueryParameter(API_KEY,apiKey).build();
            URL url = null;
            try {
                Log.e("url",uri.toString());
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                    return;
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
        protected Void doInBackground(String... params) {
            if(params[0].equals("do"))
                Log.e("ayway yasya","aaaaaaaaa");
            getTrailers(params[0]);
            getMovieReviews(params[0]);
            movieID = params[0];
            return null;
        }

        private void processResponse(String response) throws JSONException {


            JSONObject ob = new JSONObject(response);
            JSONArray results = ob.getJSONArray("results");
            Trailer trailer;
            for (int i=0; i<results.length();i++){
                JSONObject tempJSON = results.getJSONObject(i);
                // Log.e("JSON object array " + i,tempJSON.toString());
                trailer = new Trailer();
                trailer.setURL(tempJSON.getString("key"));
                trailerList.add(trailer);
            }
        }

        private void processResponseReviews(String response) throws JSONException{
            JSONObject ob = new JSONObject(response);
            JSONArray results = ob.getJSONArray("results");
            MovieReview review;
            for (int i=0; i<results.length();i++){
                JSONObject tempJSON = results.getJSONObject(i);
                // Log.e("JSON object array " + i,tempJSON.toString());
                review = new MovieReview();
                review.setAuthorName(tempJSON.getString("author"));
                review.setReviewBody(tempJSON.getString("content"));
                reviewsList.add(review);
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Download complete. Let us update UI
            LayoutInflater vi = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout lin = (LinearLayout) mActivity.findViewById(R.id.movie_detail);

            for(int i=0;i<trailerList.size();i++){
                final Trailer tempTrailer = trailerList.get(i);
                View custom = vi.inflate(R.layout.trailers_list_item,null);
                lin.addView(custom);
                TextView temp = (TextView) custom.findViewById(R.id.trailerText);
                ImageButton ib = (ImageButton) custom.findViewById(R.id.PlayTrailer);
                ib.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MovieDetailActivity.this,tempTrailer.getURL(),Toast.LENGTH_SHORT).show();
                        Uri webpage = Uri.parse(tempTrailer.getURL());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(webpage);
                        if (intent.resolveActivity(mPackageManger) != null) {
                            startActivity(intent);
                        }

                    }
                });

                temp.setText("Trailer " + (i+1));
                temp.setId(View.generateViewId());
            }
            if(!trailerList.isEmpty() && mShareActionProvider!=null) {
                mMovieString = trailerList.get(0).getURL();
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }else{mMovieString = null;}

            // Get Reviews
            LayoutInflater vil = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout li = (LinearLayout) mActivity.findViewById(R.id.movie_detail);
            for(int i=0; i<reviewsList.size();i++){
                final MovieReview tempReview = reviewsList.get(i);
                View custom = vil.inflate(R.layout.review_list_item,null);
                li.addView(custom);
                TextView author = (TextView) custom.findViewById(R.id.authorName);
                author.setText(tempReview.getAuthorName());
                TextView content = (TextView) custom.findViewById(R.id.reviewContent);
                content.setText(tempReview.getReviewBody());
            }
        }
    }
}
