package com.epbit.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.epbit.constants.ProjectURls;
import com.epbit.fragments.RideFragment;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.utils.SharedPreferencesUtility;

import org.json.JSONObject;

import java.util.HashMap;

public class Update_user_loc_task extends AsyncTask<String, String, String> {
    private Context context;
    private RideFragment rideFragment;
    private LinearLayout ridenowlaterlayout;
    private String TAG = "updateloctask";
    private String response = "";
    UserData userdata;

    public Update_user_loc_task(Context context, LinearLayout ridenowlaterlayout, RideFragment rideFragment) {
        this.context = context;
        this.rideFragment = rideFragment;
        this.ridenowlaterlayout = ridenowlaterlayout;
        userdata = UserData.getinstance(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        ridenowlaterlayout.setVisibility(View.INVISIBLE);
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.UPDATE_USER_LOCATION_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("email", SharedPreferencesUtility.loadUsername(context));
        keyValue.put("lat", "" + userdata.getSourc_lat());
        keyValue.put("long", "" + userdata.getSourc_longt());
        keyValue.put("cabtype", userdata.getCab_type());
        keyValue.put("distance", userdata.getDistance());
        Log.d(TAG, "" + keyValue);
        try {
            FetchUrl findLocation = new FetchUrl();
            response = findLocation.fetchUrl(url, keyValue);
            String json = response;
            JSONObject jsonresult = new JSONObject(json);
            DriverDetails.setCabNumber("NOT FOUND");
            DriverDetails.setDriverName("NOT FOUND");
            DriverDetails.setDriverNumber("NOT FOUND");
            DriverDetails.setNearesCabReachingTime("NOT FOUND");
            DriverDetails.setNearestCabDistance((int) Float
                    .parseFloat(jsonresult
                            .getString("distance")));
            int time = 0;
            time = (jsonresult.getInt("reach_time"));
            int hour = 0;
            hour = time / 60;
            int min = time % 60;
            if (hour == 0) {
                DriverDetails.setNearesCabReachingTime("" + min
                        + " min");
            } else {
                DriverDetails.setNearesCabReachingTime("" + hour
                        + " h " + min + " min");
            }
            DriverDetails.setNearesCabReachingTime(""
                    + (jsonresult.getInt("reach_time"))
                    + " min");
            DriverDetails.setCabNumber(jsonresult
                    .getString("cab_number"));
            DriverDetails.setDriverName(jsonresult
                    .getString("name"));
            DriverDetails.setDriverNumber(jsonresult
                    .getString("number"));
            DriverDetails.setDriver_email(jsonresult.getString("email"));
            DriverDetails.setFare(jsonresult.getString("fare"));
            DriverDetails.setFarePerUnit(jsonresult.getString("fare_per_unit"));
            Log.d("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] ele = e.getStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String response) {
        try {
            String json = response;
            JSONObject obj = new JSONObject(json);
            if (DriverDetails.getDriverNumber().equals("NOT FOUND")
                    && DriverDetails.getCabNumber().equals("NOT FOUND")
                    || DriverDetails.getDriverNumber().length() <= 1
                    && DriverDetails.getCabNumber().length() <= 1) {
                ((RideFragment) rideFragment).responseFirst();
            } else {
                ((RideFragment) rideFragment).responseSecond();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
