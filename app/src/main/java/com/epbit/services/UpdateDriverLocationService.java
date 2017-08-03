package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.epbit.constants.ProjectURls;
import com.epbit.latlong.LatLongDetails;
import com.epbit.utils.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateDriverLocationService extends AsyncTask<String, String, String> {
    String response;
    ProgressDialog dialog;
    private JSONObject json;
    private Context context;
    private double lat, longt;
    private Location location;
    private GPSTracker gpsTracker;
    String tag = "UpdateDriverLocationAsyntask";

    public UpdateDriverLocationService(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            gpsTracker = new GPSTracker(context);
            location = gpsTracker.getLocation();
            if (gpsTracker.canGetLocation())
                lat = location.getLatitude();
            longt = location.getLongitude();
            LatLongDetails.driver_latitude = lat;
            LatLongDetails.driver_longitude = longt;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = ProjectURls.GET_DRIVER_CURRENT_LOCATION;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("driver_id", "" + (params[0]));
        keyValue.put("lat", ""
                + LatLongDetails.driver_latitude);
        keyValue.put("long", ""
                + LatLongDetails.driver_longitude);
        FetchUrl fetchUrl = new FetchUrl();
        response = fetchUrl.fetchUrl(url, keyValue);
        Log.d(tag, response);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject responJsonObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("EXCEPTION", "Parsing update_driver location result");
            e.printStackTrace();
        }
    }
}
