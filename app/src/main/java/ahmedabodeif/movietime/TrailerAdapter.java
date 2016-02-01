package ahmedabodeif.movietime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmedabodeif1 on 1/30/16.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    Context context;
    ArrayList<Trailer> data;
    int layoutResourceId;


    public TrailerAdapter(Context context, int resource, ArrayList<Trailer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = (ArrayList<Trailer>) objects;
        this.layoutResourceId = resource;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Row holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Row();
            holder.tx = (TextView) row.findViewById(R.id.trailerText);
            row.setTag(holder);
        } else {
            holder = (Row) row.getTag();
        }
        Trailer item = data.get(position);
        holder.tx.setText("Trailer " + (position+1));
        return row;
    }


    static class Row{
        TextView tx;
    }
}
