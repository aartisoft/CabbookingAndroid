package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.epbit.constants.ProjectURls;
import com.epbit.latlong.LatLongDetails;
import com.epbit.model.DriverDetails;
import com.epbit.utils.SharedPreferencesUtility;

import java.util.HashMap;

public class Driver_loc_update_task extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "Gcmtask";
    private String response = "";
    private ProgressDialog dialog;
    String newMessage;

    public Driver_loc_update_task(Context context, String newMessage) {
        this.context = context;
        this.newMessage = newMessage;
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.DRIVER_UPDATE_LOC_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("d_email", SharedPreferencesUtility.loadUsername(context));
        keyValue.put("d_lat", "" + LatLongDetails.driver_latitude);
        keyValue.put("d_long", "" + LatLongDetails.driver_longitude);
        keyValue.put("noti_user_email", newMessage);
        keyValue.put("driver_status", DriverDetails.getDriverStatus());
        keyValue.put("d_cabtype", DriverDetails.getDriverCabType());
        Log.d(TAG, "" + keyValue);
        try {
            FetchUrl findLocation = new FetchUrl();
            response = findLocation.fetchUrl(url, keyValue);
            Log.d("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] ele = e.getStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String response) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
