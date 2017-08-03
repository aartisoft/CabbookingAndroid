package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.utils.SharedPreferencesUtility;

import org.json.JSONObject;

import java.util.HashMap;

public class GetDriverStatus extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "getdriverstatustask";
    private String response = "";
    private ProgressDialog dialog;
    private LinearLayout statuslinearlayout;
    private ImageView driverStatusimageview;
    private TextView driverStatustextview;
    private DriverDetails driverDetails;

    public GetDriverStatus(Context context, ImageView driverStatusimageview, TextView driverStatustextview, LinearLayout statuslinearlayout) {
        this.context = context;
        this.driverStatusimageview = driverStatusimageview;
        this.driverStatustextview = driverStatustextview;
        this.statuslinearlayout = statuslinearlayout;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        statuslinearlayout.setVisibility(View.INVISIBLE);
        dialog = ProgressDialog.show(context, null,
                context.getString(R.string.get_d_status_dialog), true, false);
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.GET_DRIVER_STATUS_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("d_email", SharedPreferencesUtility.loadUsername(context));
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
            String json = response;
            JSONObject obj = new JSONObject(json);
            String status = obj.getString("driver_status");
            String cabType = obj.getString("cab_type");
            if (cabType.equalsIgnoreCase("7")) {
                DriverDetails.setDriverCabType("1");
            } else if (cabType.equalsIgnoreCase("8")) {
                DriverDetails.setDriverCabType("2");
            } else {
                DriverDetails.setDriverCabType("3");
            }
            if (status.contains("available")) {
                statuslinearlayout.setVisibility(View.VISIBLE);
                driverStatusimageview.setImageResource(R.drawable.available);
                driverStatustextview.setText("Available");
                DriverDetails.setDriverStatus("available");
            } else if (status.contains("pending")) {
                statuslinearlayout.setVisibility(View.VISIBLE);
                driverStatusimageview.setImageResource(R.drawable.pending);
                driverStatustextview.setText("Pending");
                DriverDetails.setDriverStatus("pending");
            } else if (status.contains("booked")) {
                statuslinearlayout.setVisibility(View.VISIBLE);
                driverStatusimageview.setImageResource(R.drawable.booked);
                driverStatustextview.setText("Booked");
                DriverDetails.setDriverStatus("booked");
            } else {
                statuslinearlayout.setVisibility(View.VISIBLE);
                driverStatusimageview.setImageResource(R.drawable.available);
                driverStatustextview.setText("No Status Available !");
                DriverDetails.setDriverStatus("no status available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
