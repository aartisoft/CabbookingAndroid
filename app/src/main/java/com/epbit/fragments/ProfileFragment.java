package com.epbit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.constants.ProjectURls;
import com.epbit.helper.MyWebViewClient;
import com.epbit.login.LoginDetails;
import com.epbit.utils.SharedPreferencesUtility;

public class ProfileFragment extends BaseFragment {
    private Handler mHandler = new Handler();
    WebView profile_web_view;
    ProgressBar profileProgressupdateProfile;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.item_one));
        context = getActivity();
        try {
            profileProgressupdateProfile = (ProgressBar) root.findViewById(R.id.progressBarupdateprofile);
            profile_web_view = (WebView) root.findViewById(R.id.profile_webview);
            MyWebViewClient.enableWebViewSettings(profile_web_view);
            profile_web_view.addJavascriptInterface(new DemoJavaScriptInterface(context), "uploadpic");
            profileProgressupdateProfile.setVisibility(View.GONE);
            profile_web_view.setVisibility(View.VISIBLE);
            profile_web_view.setWebViewClient(new MyWebViewClient(context));
            profile_web_view.loadUrl(ProjectURls.getProfileUrl(SharedPreferencesUtility.loadUsername(context),
                    SharedPreferencesUtility.loadUserType(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface(Context c) {
        }

        @JavascriptInterface
        public void clickOnUploadPic() {
            mHandler.post(new Runnable() {
                public void run() {
                    replaceFragment(context, new UpdateProfilePictureFragment());
                }
            });
        }

        @JavascriptInterface
        public void clickOnDriver(String fullname, String contact,
                                  String cab_type, String cab_number) {
            LoginDetails.CabType = Integer.parseInt(cab_number);
            SharedPreferencesUtility.saveCabType(context, LoginDetails.CabType);
            mHandler.post(new Runnable() {
                public void run() {
                }
            });
        }
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
}
