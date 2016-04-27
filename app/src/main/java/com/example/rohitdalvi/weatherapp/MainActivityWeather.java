package com.example.rohitdalvi.weatherapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import Model.Event;
import Util.Prefs;

public class MainActivityWeather extends AppCompatActivity {

    TextView titletext;
    TextView temptext;
    TextView conditiontext;
    TextView lastUpdatetext;
    TextView speedtext;
    TextView humiditytext;
    TextView sunrisetext;
    TextView sunsettext;


    Event event;

        //final String finalurl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22bridgeport%2C%20ct%22)&format=json";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titletext = (TextView)findViewById(R.id.titleId);
        temptext = (TextView)findViewById(R.id.temp);
        conditiontext = (TextView)findViewById(R.id.conditionID);
        lastUpdatetext = (TextView)findViewById(R.id.lastUpdatedID);
        speedtext = (TextView)findViewById(R.id.speedID);
        humiditytext = (TextView)findViewById(R.id.humidityID);
        sunrisetext = (TextView)findViewById(R.id.sunriseID);
        sunsettext = (TextView)findViewById(R.id.sunsetID);

        Prefs cityPreference = new Prefs(MainActivityWeather.this);



        getWeather(cityPreference.getCity());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    public void getWeather(String city) {

        //final String finalurl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22bridgeport%2C%20ct%22)&format=json";
        String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")", city);
        String finalurl = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));




        JsonObjectRequest eventsRequest = new JsonObjectRequest(Request.Method.GET,
                finalurl, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    //get whole data in queryObject
                    JSONObject queryObject = response.getJSONObject("query");
                    //get results from query
                    JSONObject resultsObject = queryObject.getJSONObject("results");
                    //get channel from results
                    JSONObject channelObject = resultsObject.getJSONObject("channel");

                    String title = channelObject.getString("title");

                    JSONObject itemObject = channelObject.getJSONObject("item");
                    JSONObject conditionObject = itemObject.getJSONObject("condition");

                    String temperature = conditionObject.getString("temp");
                    String text = conditionObject.getString("text");



                    String lastUpdated = channelObject.getString("lastBuildDate");

                    //get location from channel
                    JSONObject locaObject = channelObject.getJSONObject("location");

                    String city = locaObject.getString("city");
                    String country = locaObject.getString("country");
                    String region = locaObject.getString("region");

                    //get wind from channel
                    JSONObject windObject = channelObject.getJSONObject("wind");
                    String speed = windObject.getString("speed");

                    //get atmopsphere from channel
                    JSONObject atmObject = channelObject.getJSONObject("atmosphere");
                    String humidity = atmObject.getString("humidity");

                    //get astronomy from channel
                    JSONObject astronomyObject = channelObject.getJSONObject("astronomy");
                    String sunrise = astronomyObject.getString("sunrise");
                    String sunset  = astronomyObject.getString("sunset");


                    Event event = new Event();

                    event.setTitle(title);
                    event.setTemperature(temperature);
                    event.setCondition(text);
                    event.setLastUpdate(lastUpdated);
                    event.setSpeed(speed);
                    event.setHumidity(humidity);
                    event.setSunrise(sunrise);
                    event.setSunset(sunset);


                    titletext.setText("Title: " + event.getTitle());
                    temptext.setText("Temperature: " + event.getTemperature() + "° F");
                    conditiontext.setText("Condition: " + event.getCondition());
                    lastUpdatetext.setText("Last Updated: " + event.getLastUpdate());
                    speedtext.setText("Speed: " + event.getSpeed() + " mph");
                    humiditytext.setText("Humidity: " + event.getHumidity() + " %");
                    sunrisetext.setText("Sunrise: " + event.getSunrise());
                    sunsettext.setText("Sunset: " + event.getSunset());


















                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(eventsRequest);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_city) {
            showInputCity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInputCity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityWeather.this);
        builder.setTitle("Put you City");
        final EditText cityInput = new EditText(MainActivityWeather.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("bridgeport, ct");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Prefs cityPreference = new Prefs(MainActivityWeather.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                getWeather(newCity);

            }
        });
        builder.show();
    }
}
