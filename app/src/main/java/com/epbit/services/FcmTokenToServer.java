package com.epbit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.utils.SharedPreferencesUtility;

import java.util.HashMap;

public class FcmTokenToServer extends AsyncTask<String, String, String> {
    private Context context;
    private String TAG = "Gcmtask";
    private String response = "";
    private ProgressDialog dialog;

    public FcmTokenToServer(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources()
                .getString(R.string.please_wait));
        dialog.show();
    }

    protected String doInBackground(String... params) {
        String url = ProjectURls.SERVER_URL;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        keyValue.put("email", SharedPreferencesUtility.loadUsername(context));
        keyValue.put("regId", SharedPreferencesUtility.loadRegistrationId(context));
        keyValue.put("user_type", SharedPreferencesUtility.loadUserType(context));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
