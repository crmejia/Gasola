package com.crmejia.gasola;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by crismarmejia on 3/28/15.
 */
public class LogAdapter extends CursorAdapter {

    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder{
        public final TextView startDateView;
        public final TextView endDateView;
        public final TextView distanceView;
        public final TextView quantityView;
        public final ImageButton deleteButtonView;

        public ViewHolder(View view){
            startDateView = (TextView)view.findViewById(R.id.list_item_log_start_date_textView);
            endDateView = (TextView)view.findViewById(R.id.list_item_log_end_date_textView);
            distanceView = (TextView)view.findViewById(R.id.list_item_log_distance_textView);
            quantityView = (TextView)view.findViewById(R.id.list_item_log_quantity_textView);
            deleteButtonView = (ImageButton)view.findViewById(R.id.list_item_log_delete_imageButton);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_log, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String startDate = String.format("From %s",Utility.getFriendlyDayString(context,cursor.getString(MainFragment.COL_LOG_START_DATE)));
        String endDate = "";
        String gasAmount =  Utility.formattedAmount(cursor.getInt(MainFragment.COL_LOG_GAS_AMOUNT),context);
        int startDistance = cursor.getInt(MainFragment.COL_LOG_START_DISTANCE);
        int endDistance = cursor.getInt(MainFragment.COL_LOG_END_DISTANCE);
        String totalDistance = "Logging...";

        if(endDistance > 0){
            endDate = String.format("Until %s", Utility.getFriendlyDayString(context, cursor.getString(MainFragment.COL_LOG_END_DATE)));
            totalDistance = Utility.formattedTotalDistance(startDistance, endDistance, context);
        }

        viewHolder.startDateView.setText(startDate);
        viewHolder.endDateView.setText(endDate);
        viewHolder.quantityView.setText(gasAmount);
        viewHolder.distanceView.setText(totalDistance);
    }
}
