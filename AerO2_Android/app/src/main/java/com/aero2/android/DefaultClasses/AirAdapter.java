package com.aero2.android.DefaultClasses;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aero2.android.DefaultActivities.MainActivity;
import com.aero2.android.R;


/**
 * {@link com.muddassir.android.pulsarbeta.HobbieAdapter} exposes a list of hobbies
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class AirAdapter extends CursorAdapter {
    public AirAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //This adapter will be used to make a list view of Hobbies from the data base

    private final int VIEW_TYPE_HOBBIE = 1;
    private final int VIEW_TYPE_WORK = 0;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int workOrJob) {
        return (workOrJob == 0) ? VIEW_TYPE_WORK : VIEW_TYPE_HOBBIE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = R.layout.air_list_item;
        view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;

    }


    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String smogValue = cursor.getString(MainActivity.COLUMN_SMOG_VALUE);
        String airQuality=cursor.getString(MainActivity.COLUMN_AIR_QUALITY);
        String time = cursor.getString(MainActivity.COLUMN_TIME);
        String longitude = cursor.getString(MainActivity.COLUMN_LONG);
        String latitude = cursor.getString(MainActivity.COLUMN_LAT);
        String altitude = cursor.getString(MainActivity.COLUMN_ALT);

        String AirText =
                "Quantity of Smog" + " : " + smogValue + " \n" +
                        "Air Quality : "+airQuality+" \n"+
                        "Time" + " : " + time + " \n"
                        + "Coordinates" + " : " + longitude + "   " + latitude + "   " + altitude;
        viewHolder.tv.setText(AirText);

//        TextView tv = (TextView)view;
//      tv.setText(convertCursorRowToUXFormat(cursor));
    }

    public static class ViewHolder {
        public final TextView tv;

        public ViewHolder(View view) {
            tv = (TextView) view.findViewById(R.id.item_text_air);
        }
    }
}
