package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.utils.SharedPreferencesUtility;
import org.json.JSONObject;
import java.util.HashMap;

public class RideNowConfirmService extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "RideNowConfirmtask";
    private String response = "";
    private ProgressDialog dialog;
    private UserData userData;

    public RideNowConfirmService(Context context) {
        this.context = context;
        userData = UserData.getinstance(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources()
                .getString(R.string.please_wait));
        dialog.show();
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.RIDE_NOW_CONFIRM_URL;
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("email", SharedPreferencesUtility.loadUsername(context));
        keyValue.put("d_email", DriverDetails.getDriver_email());
        keyValue.put("d_cabtype", DriverDetails.getDriverCabType());
        keyValue.put("pick_address", userData.getAddress());
        keyValue.put("pick_date", "" + today.year + "-" + (today.month + 1) + "-" + today.monthDay);
        keyValue.put("pick_time", "" + today.hour + ":" + today.minute);
        keyValue.put("dest_address", userData.getDesti_address());
        keyValue.put("distance", userData.getDistance());
        keyValue.put("cab_number", DriverDetails.getCabNumber());
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
            userData.setUnique_Table_ID(obj.getString("unique_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
