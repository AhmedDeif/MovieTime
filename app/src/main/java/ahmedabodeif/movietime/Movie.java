package ahmedabodeif.movietime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by ahmedabodeif1 on 1/21/16.
 */
public class Movie implements Serializable {


    int _id;
    private final String baseURL = "http://image.tmdb.org/t/p/w185//";
    private Bitmap moviePoster;
    private String title;
    private String posterURL;
    private String overview;
    private String rating;
    private String realseDate;
    private String length;
    private String movieId;
     byte[] _image;




    public Movie(){
        super();
    }

    public Movie(int id){
        super();
        this._id = id;
    }

    public void setImage(byte[] m)
    {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(m);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        this.moviePoster = theImage;
    }

    public Movie(Bitmap img){
        this.moviePoster = img;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }


    public Bitmap getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(Bitmap moviePoster) {
        this.moviePoster = moviePoster;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        moviePoster.compress(Bitmap.CompressFormat.PNG, 100, bos);
        this._image = bos.toByteArray();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterURL() {
        return baseURL + posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRealseDate() {
        return realseDate;
    }

    public void setRealseDate(String realseDate) {
        this.realseDate = realseDate;
    }
}
