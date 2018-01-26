package com.example.vineelabhinav.stormyversion2.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vineelabhinav.stormyversion2.R;
import com.example.vineelabhinav.stormyversion2.adapters.DayAdapter;
import com.example.vineelabhinav.stormyversion2.weather.Day;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {

    public static final String TAG=DailyForecastActivity.class.getSimpleName();
    private Day[] mDays;
    TextView locationLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        Intent intent=getIntent();
        Parcelable[] parcelables=intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays= Arrays.copyOf(parcelables,parcelables.length,Day[].class);
        DayAdapter adapter=new DayAdapter(this,mDays);
        locationLabel=findViewById(R.id.locationLabel);
        locationLabel.setText(MainActivity.locality);
        setListAdapter(adapter);

        /* Simple ArrayAdapter to show data in ListView

        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,daysOfTheWeek);
        setListAdapter(adapter);  */
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek=mDays[position].getDayOfTheWeek();
        String conditions=mDays[position].getSummary();
        String highTemp=mDays[position].getTemperatureMax()+"";
        String message=String.format("On %s the high will be %s and it will be %s",dayOfTheWeek,highTemp,conditions);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }
}
