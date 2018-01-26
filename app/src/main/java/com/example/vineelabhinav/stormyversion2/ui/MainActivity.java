package com.example.vineelabhinav.stormyversion2.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vineelabhinav.stormyversion2.R;
import com.example.vineelabhinav.stormyversion2.weather.Current;
import com.example.vineelabhinav.stormyversion2.weather.Day;
import com.example.vineelabhinav.stormyversion2.weather.Forecast;
import com.example.vineelabhinav.stormyversion2.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener{

    public static final String TAG=MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST="DAILY_FORECAST";
    public static final String HOURLY_FORECAST="HOURLY_FORECAST";
    private Forecast mForecast;
    private Double latitude;
    private Double longitude;
    public static String locality;

    private String provider;
    private LocationManager mLocationManager;
    Criteria criteria;

    AlertDialog dialog;


    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView)ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.locationLabel) TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);


        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    getLocation();
                    if(latitude!=null && longitude!=null) {
                        getLocalityName(latitude, longitude);
                        getForecast(latitude, longitude);
                    }
                }
                else
                    Toast.makeText(MainActivity.this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
            }
        });
        if(isNetworkAvailable()) {
            getLocation();
            if(latitude!=null && longitude!=null) {
                getLocalityName(latitude, longitude);
                getForecast(latitude, longitude);
            }
        }
        else
            Toast.makeText(MainActivity.this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.d(TAG,"In OnResume Method");
        if(dialog!=null) {
            dialog.dismiss();
            dialog = null;
        }
        if(provider!=null && mLocationManager!=null) {
           mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            getLocation();
            if(latitude!=null && longitude!=null) {
                getLocalityName(latitude, longitude);
                getForecast(latitude, longitude);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"In OnPause Method");
        if(mLocationManager!=null)
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = (location.getLatitude());
        longitude = (location.getLongitude());
        Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled New Provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, provider+ " Has Been Disabled ",
                Toast.LENGTH_SHORT).show();
        TurnGPS();
    }

    /* Gives an Alert Dialog Which Asks User To Turn On GPS */
    public void TurnGPS() {
        try {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Log.d(TAG, "FUCK3");
            builder.setTitle("Current Location").setMessage("Enable access my location under my location under Settings");
            builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            Log.d(TAG,"In TurnGPS Method");
            dialog = builder.create();
            dialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* Gives The Locality Name Of Given Latitude And Longitude */
    public void getLocalityName(Double latitude,Double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj=addresses.get(0);
            Log.d(TAG,obj.getAdminArea()+",,"+obj.getSubAdminArea()+",,"+obj.getLocality());
            locality=obj.getLocality();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

     /* This Function Makes an API Call to Darksky Forecast To Fetch Current Weather Data */
    public void getForecast(double latitude,double longitude) {
        String apiKey="dc5ab389de96c9861869ba1e4606733b";  /* API Key for Weather Forecast */

        String forecastUrl="https://api.darksky.net/forecast/"+apiKey+"/"+latitude+","+longitude;

        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);

        /*  Asynchronous call */
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData=response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast =parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay(mForecast);
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught:", e);
                        //e.printStackTrace();
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception Caught:", e);
                    }
                }
            });
        }
        else
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();

        /*   SYNCHRONOUS NETWORK CALL  */
        /*try {
            Response response=call.execute();
            if(response.isSuccessful())
                Log.v(TAG,response.body().string());
        } catch (IOException e) {
            Log.e(TAG,"Exception Caught:",e);
            //e.printStackTrace();
        }*/

    }

    /* Sets On And Off Progress Bar and Refresh Button While Recieving Data */
    public void toggleRefresh() {

        if(mProgressBar.getVisibility()==View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    /*Updates the UI With Current Weather Data */
    public void updateDisplay(Forecast mForecast) {
        Current current =mForecast.getCurrent();

        mTemperatureLabel.setText(current.getTemperature()+ "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity()+ "");
        mPrecipValue.setText(current.getPrecipChance()+"%");
        mSummaryLabel.setText(current.getSummary());
        mLocationLabel.setText(locality);
        Drawable drawable=getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }


    private Forecast parseForecastDetails(String jsonData) throws JSONException
    {
        Forecast forecast=new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        JSONObject daily=forecast.getJSONObject("daily");
        JSONArray data=daily.getJSONArray("data");
        Day [] days=new Day[data.length()];

        for(int i=0;i<data.length();i++)
        {
            JSONObject jsonday=data.getJSONObject(i);
            Day day=new Day();
            day.setSummary(jsonday.getString("summary"));
            day.setIcon(jsonday.getString("icon"));
            day.setTemperatureMax(jsonday.getDouble("temperatureMax"));
            day.setTime(jsonday.getLong("time"));
            day.setTimeZone(timezone);
            days[i]=day;
        }
        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        JSONObject hourly=forecast.getJSONObject("hourly");
        JSONArray data=hourly.getJSONArray("data");

        Hour [] hours=new Hour[data.length()];
        for(int i=0;i<data.length();i++)
        {
            JSONObject jsonhour=data.getJSONObject(i);
            Hour hour=new Hour();
            hour.setSummary(jsonhour.getString("summary"));
            hour.setTemperature(jsonhour.getDouble("temperature"));
            hour.setIcon(jsonhour.getString("icon"));
            hour.setTime(jsonhour.getLong("time"));
            hour.setTimeZone(timezone);
            hours[i]=hour;
        }
        return hours;
    }

    /* Sets The Current Object With Appropriate Data */
    public Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        Log.i(TAG,"From Json"+timezone);

        JSONObject currently=forecast.getJSONObject("currently");

        Current current =new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    /* Checks if Mobile Has Internet ON */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        boolean isAvailabe=false;
        if(networkInfo!=null && networkInfo.isConnected())
            isAvailabe=true;
        return isAvailabe;
    }

    /*Alerts User About Any Netwok issues */
    public void alertUserAboutError() {
        AlertDialogFragment dialog=new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    /* Gets The Present Location's Latitude And Longitude */
    public void getLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = LocationManager.NETWORK_PROVIDER;

        Location location = mLocationManager.getLastKnownLocation(provider);
        Log.d(TAG,"In getLocation");

        if (location != null) {
            System.out.println("Provider " + provider + " Has Been Selected.");
            onLocationChanged(location);
        } else {
            Log.d(TAG, "Location Not Available");
            TurnGPS();
        }
    }

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View view)
    {
        Intent intent=new Intent(this,DailyForecastActivity.class);
        Day [] x=mForecast.getDailyForecast();
        intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());
        startActivity(intent);

    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view)
    {
        Intent intent=new Intent(this,HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST,mForecast.getHourlyForecast());
        startActivity(intent);
    }
}
