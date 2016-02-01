package ahmedabodeif.movietime;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmedabodeif1 on 1/21/16.
 */
public class GridAdapter extends ArrayAdapter<Movie> {


    Context context;
    ArrayList<Movie> data;
    int layoutResourceId;

    public GridAdapter(Context context, int resource, ArrayList<Movie> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    public void setGridData(ArrayList<Movie> mGridData) {
        this.data = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Row holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Row();
            holder.imgView = (ImageView) row.findViewById(R.id.moviePoster);
            row.setTag(holder);
        } else {
            holder = (Row) row.getTag();
        }
        final Movie item = data.get(position);

        if(item.getMoviePoster()!=null) {
            holder.imgView.setImageBitmap(item.getMoviePoster());
        }
        else {


            final Row finalHolder = holder;
            Picasso.with(context).load(item.getPosterURL()).into(holder.imgView, new com.squareup.picasso.Callback() {
                @Override
                public void onError() {

                }

                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) finalHolder.imgView.getDrawable()).getBitmap();
                    item.setMoviePoster(bitmap);
                    Log.e("Picasso adding image", "done");
                }
            });
        }
        return row;
    }

    static class Row {
        ImageView imgView;
    }
}

