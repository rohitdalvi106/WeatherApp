package Util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Rohitdalvi on 4/18/2016.
 */
public class Prefs {

    SharedPreferences preferences;
    public Prefs(Activity activity){
    preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return preferences.getString("city", "bridgeport, ct");
    }

    public void setCity(String city){
        preferences.edit().putString("city", city).commit();
    }



}
