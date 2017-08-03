package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.model.DriverDetails;
import com.epbit.utils.SharedPreferencesUtility;

import org.json.JSONObject;

import java.util.HashMap;

public class DriverStatusUpdate extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "driverstatustask";
    private String response = "";
    private ProgressDialog dialog;

    public DriverStatusUpdate(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources()
                .getString(R.string.updating_status));
        dialog.show();
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.DRIVER_STATUS_CHANGE_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("email", SharedPreferencesUtility.loadUsername(context));
        keyValue.put("driver_status", DriverDetails.getDriverStatus().toLowerCase());
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
            dialog.dismiss();
            JSONObject obj = new JSONObject(response);
            int result = obj.getInt("success");
            if (result == 1) {
                Toast.makeText(
                        context,
                        context.getString(R.string.status_updated)
                                + (DriverDetails.getDriverStatus().toUpperCase()
                                .charAt(0) + DriverDetails.getDriverStatus()
                                .substring(1)), Toast.LENGTH_SHORT)
                        .show();
            }
            if (result == 0) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
