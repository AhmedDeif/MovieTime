package ahmedabodeif.movietime;

/**
 * Created by ahmedabodeif1 on 1/30/16.
 */
public class Trailer {

    String URL;

    public Trailer(){}

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = "https://www.youtube.com/watch?v=" + URL;
    }
}
