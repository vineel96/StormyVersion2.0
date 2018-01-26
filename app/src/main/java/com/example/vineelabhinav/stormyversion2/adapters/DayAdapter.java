package com.example.vineelabhinav.stormyversion2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vineelabhinav.stormyversion2.R;
import com.example.vineelabhinav.stormyversion2.weather.Day;

/**
 * Created by vineel abhinav on 1/25/2018.
 */

public class DayAdapter extends BaseAdapter {

    public static final String TAG=BaseAdapter.class.getSimpleName();
    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day days[]) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null)
        {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);
            holder=new ViewHolder();
            holder.iconImageView=convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel=convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel=convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);   //Its like attaching container to convertView
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();  //Its like getting container from convertView

        }
        /* Setting new data in the container by replacing old data */

        Day day=mDays[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax()+"");
        if(position==0)
            holder.dayLabel.setText("Today");
        else
            holder.dayLabel.setText(day.getDayOfTheWeek());
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
