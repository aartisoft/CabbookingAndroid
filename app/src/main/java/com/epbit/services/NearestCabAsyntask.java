package com.epbit.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.utils.SharedPreferencesUtility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class NearestCabAsyntask extends AsyncTask<String, String, String> {
    private Context context;
    private GoogleMap googleMap;
    UserData userData;
    private SharedPreferencesUtility preferencesUtility;
    String tag = "NearestCabAsyntask";
    private String response;

    public NearestCabAsyntask(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        userData = UserData.getinstance(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String url = ProjectURls.NEAREST_CAB_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("cabtype", "" + SharedPreferencesUtility.loadCabType(context));
        keyValue.put("lat", "" + userData.getSourc_lat());
        keyValue.put("long", "" + userData.getSourc_longt());
        keyValue.put("email", SharedPreferencesUtility.loadUsername(context));
        FetchUrl fetchUrl = new FetchUrl();
        response = fetchUrl.fetchUrl(url, keyValue);
        Log.d(tag, response);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject res;
           if(response.has("response")) {
               res = response.getJSONObject("response");
               String result1 = res.getString("result");
               if (result1.equalsIgnoreCase("1")) {
                   JSONArray location = res.getJSONArray("location");
                   for (int i = 0; i < result1.length(); i++) {
                       JSONObject arrayObject = location.getJSONObject(i);
                       double lat = arrayObject.getDouble("lat");
                       double longt = arrayObject.getDouble("long");
                       LatLng position = new LatLng(lat, longt);
                       googleMap.addMarker(new MarkerOptions().position(position)
                               .icon(BitmapDescriptorFactory
                                       .fromResource(R.drawable.chauffeurs)));
                       DriverDetails.setNoCabFound("CabFound");
                   }
               }
           }
            else {
              // String error = response.getString("error");
              // Toast.makeText(context,response.toString(),Toast.LENGTH_LONG).show();
               DriverDetails.setNoCabFound("NoCabFound");
           }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
    }
}
