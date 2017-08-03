package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.utils.SharedPreferencesUtility;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginTask extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "Logintask";
    private String response = "";
    private ProgressDialog dialog;
    private String userName, password;
    private ProgressBar login_progress_bar;
    private SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance();

    public LoginTask(Context context, String userName, String password, ProgressBar login_progress_bar) {
        this.context = context;
        this.userName = userName;
        this.password = password;
        this.login_progress_bar = login_progress_bar;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources()
                .getString(R.string.please_wait));
        dialog.show();
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.LOGIN_URL_STRING;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("username", userName);
        keyValue.put("password", password);
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
            int result = obj.getInt("success");
            if (result == 1) {
                String userType = obj.getString("user_type");
                SharedPreferencesUtility.saveUsername(context, userName);
                SharedPreferencesUtility.savePassword(context, password);
                sharedPreferencesUtility.setLoginflag(context, true);
                SharedPreferencesUtility.saveUserType(context, userType);
                SharedPreferencesUtility.saveProfilePic(context, "");
                if (userType.equalsIgnoreCase("passenger")) {
                    Intent i = new Intent(context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else if (userType.equalsIgnoreCase("driver")) {
                    Intent main_to_driver_intent = new Intent(context,
                            MainActivity.class);
                    main_to_driver_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(main_to_driver_intent);
                } else {
                    Toast.makeText(context, "Usertype not found",
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (result == 0) {
                SharedPreferencesUtility.saveUsername(context, "0");
                sharedPreferencesUtility.setLoginflag(context, false);
                SharedPreferencesUtility.savePassword(context, "0");
                Toast.makeText(context, "Wrong user name or password", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
