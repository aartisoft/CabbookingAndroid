package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.model.UserData;
import com.epbit.utils.SharedPreferencesUtility;

import org.json.JSONObject;

import java.util.HashMap;

public class ConfirmRejectCabService extends AsyncTask<String, String, String> {
    private Context context, context1;
    private String TAG = "confirmrejecttask";
    private String response = "";
    private ProgressDialog dialog;
    int now_later_flag;
    UserData userdata;

    public ConfirmRejectCabService(Context context, int now_later_flag, Context context1) {
        this.context = context;
        this.now_later_flag = now_later_flag;
        this.context1 = context1;
        userdata = UserData.getinstance(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources()
                .getString(R.string.please_wait));
        dialog.show();
    }

    protected String doInBackground(String... params) {
        String url;
        if (now_later_flag == 1) {
            url = ProjectURls.CONFIRM_REJECT_Now_URL;
        } else {
            url = ProjectURls.CONFIRM_REJECT_URL;
        }
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("confirm_reject_cab", params[0]);
        keyValue.put("user_type", SharedPreferencesUtility.loadUserType(context));
        keyValue.put("unique_id", userdata.getUnique_Table_ID());
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
            String success = obj.getString("success");
            String status = obj.getString("status");
            if (success.equals("0")) {
                if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("driver")) {
                    Toast.makeText(context, "Cannot Confirm Booking!! Passenger has Rejected This Booking", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Cannot Confirm Booking!! Driver has Rejected This Booking", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context1, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            } else {
                if (status.equalsIgnoreCase("confirm")) {
                    Toast.makeText(context, "Your Ride is Confirmed", Toast.LENGTH_SHORT).show();
                    if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("passenger")) {
                       /* RideDetailsFragment fragment = new RideDetailsFragment();
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.containerView, fragment).commit();*/
                    }
                } else {
                    Toast.makeText(context, "Your Ride is Rejected", Toast.LENGTH_SHORT).show();
                    if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("passenger")) {
                        context.startActivity(new Intent(context1, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
