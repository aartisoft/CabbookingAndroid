package com.epbit.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.helper.MyPayPalService;
import com.epbit.helper.MyWebViewClient;
import com.epbit.services.UpdatePaymentDetails;
import com.epbit.utils.SharedPreferencesUtility;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONObject;

public class RideDetailsFragment extends BaseFragment {
    private Context context;
    WebView rides_webview;
    ImageButton paypalbutton;
    private Handler mHandler = new Handler();
    public static String table_id = "";
    JSONObject js;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ridedetails, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.item_two));
        context = getActivity();
        try {
            MyPayPalService.startPayPalConfigurationsService(context);
            rides_webview = (WebView) root.findViewById(R.id.rides_webview);
            MyWebViewClient.enableWebViewSettings(rides_webview);
            rides_webview.setWebChromeClient(new MyWebChromeClient());
            rides_webview.addJavascriptInterface(new DemoJavaScriptInterface(),
                    "android");
            rides_webview.setWebViewClient(new MyWebViewClient(context));

            if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("driver")) {
                rides_webview.loadUrl(ProjectURls.RIDES_Driver_URL
                        + SharedPreferencesUtility.loadUsername(context));
                Log.d("Ride Url", ProjectURls.RIDES_Driver_URL
                        + SharedPreferencesUtility.loadUsername(context));
            } else {
                rides_webview.loadUrl(ProjectURls.RIDES_Passenger_URL
                        + SharedPreferencesUtility.loadUsername(context));
                Log.d("Ride Url", "" + ProjectURls.RIDES_Passenger_URL
                        + SharedPreferencesUtility.loadUsername(context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface() {
        }

        @JavascriptInterface
        public void clickOnMakePayment(final String amount, final String payment_id,
                                       final String currency) {
            mHandler.post(new Runnable() {
                public void run() {
                    try {
                        if (Float.parseFloat(amount) > 0) {
                            makePaymentDialog(payment_id);
                        } else {
                            Toast.makeText(context,
                                    "Amount Should be Greater than 0",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @JavascriptInterface
        public void trackDriver(final String driver_Id) {
            mHandler.post(new Runnable() {
                public void run() {
                    try {
                        TrackDriverFragment trackDriverFragment = new TrackDriverFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("driver_id", driver_Id);
                        trackDriverFragment.setArguments(bundle);
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.containerView, trackDriverFragment).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void makePaymentDialog(final String payment_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.payment_successful)
                .setTitle("Thank You")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new UpdatePaymentDetails(context,rides_webview,payment_id).execute("");
                       /* context.startActivity(new Intent(context, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));*/
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        js = MyPayPalService.processPayPalResponse(context,
                requestCode, resultCode, data, rides_webview);
        if (js == null) {
            rides_webview.loadUrl(ProjectURls.PAYMENT_FAIL_URL);
        } else {
           /* new UpdatePaymentDetails(context, js, table_id,
                    rides_webview).execute("");*/
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        context.stopService(new Intent(context, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void backPressed() {
        try {
            context.startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            result.confirm();
            return true;
        }
    }
}
