package com.example.vineelabhinav.stormyversion2.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.example.vineelabhinav.stormyversion2.R;
import com.example.vineelabhinav.stormyversion2.adapters.HourAdapter;
import com.example.vineelabhinav.stormyversion2.weather.Hour;

import java.lang.reflect.Array;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {

    private Hour[] mHours;

    @BindView(R.id.recyclerView)RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        Parcelable [] parcelables=intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours= Arrays.copyOf(parcelables,parcelables.length,Hour[].class);
        HourAdapter adapter=new HourAdapter(this,mHours);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);/* since our items has fixed number of views we set this property so that performance increases */
    }
}
