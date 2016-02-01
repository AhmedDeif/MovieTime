package ahmedabodeif.movietime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by ahmedabodeif1 on 1/31/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "MovieTimeDatabase";

    private static final String TABLE_MOVIES = "movies";

    // Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "MovieTitle";
    private static final String KEY_OVERVIEW = "MovieOverview";
    private static final String KEY_RATING = "MovieRating";
    private static final String KEY_DATE = "MovieDate";
    private static final String KEY_IMAGE = "MoviePoster";

    private static final String[] allFields = new String[]{
            KEY_ID, KEY_NAME, KEY_OVERVIEW, KEY_RATING, KEY_DATE, KEY_IMAGE};



    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTable = "CREATE TABLE " + TABLE_MOVIES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " varchar(255)," +
                KEY_OVERVIEW + " TEXT," +
                KEY_RATING + " varchar(255)," +
                KEY_DATE + " varchar(255)," +
                KEY_IMAGE + " BLOB" +
                ")";
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

    //*****************     CRUD OPERATIONS     *****************//


    //  get
    Movie getMovie(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIES, allFields, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Movie movie = new Movie();
        movie.setMovieId(cursor.getString(0));
        movie.setTitle(cursor.getString(1));
        movie.setOverview(cursor.getString(2));
        movie.setRating(cursor.getString(3));
        movie.setRealseDate(cursor.getString(4));
        movie.setImage(cursor.getBlob(5));

        return movie;
    }

    //  add
    void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,movie.getMovieId());
        values.put(KEY_NAME, movie.getTitle());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_RATING, movie.getRating());
        values.put(KEY_DATE, movie.getRealseDate());
        values.put(KEY_IMAGE, movie._image);

        db.insert(TABLE_MOVIES, null, values);
        db.close();
    }

    //  index
    public ArrayList<Movie> getAll(){
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        String selectQuery = "SELECT * FROM "+ TABLE_MOVIES ;//" ORDER BY name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setMovieId(cursor.getString(0));
                movie.setTitle(cursor.getString(1));
                movie.setOverview(cursor.getString(2));
                movie.setRating(cursor.getString(3));
                movie.setRealseDate(cursor.getString(4));
                movie.setImage(cursor.getBlob(5));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }
        db.close();
        return movieList;
    }

    //  update
    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, movie.getMovieId());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_RATING, movie.getRating());
        values.put(KEY_DATE, movie.getRealseDate());
        values.put(KEY_IMAGE, movie._image);

        return db.update(TABLE_MOVIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getMovieId()) });
    }

    //  delete
    public void deleteMovie(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}
