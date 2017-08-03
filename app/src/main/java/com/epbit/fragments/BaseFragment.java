package com.epbit.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.helper.MyWebViewClient;

public abstract class BaseFragment extends Fragment {
    @Override
    public void onStop() {
        System.gc();
        super.onStop();
    }

    public void replaceFragment(Context context, Fragment fragment) {
        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    backPressed();
                    return true;
                }
                return false;
            }
        });
    }

    protected abstract void backPressed();

    public void setWebViewClient(Context context, WebView web) {
        web.setWebViewClient(new MyWebViewClient(context));
        MyWebViewClient.enableWebViewSettings(web);
    }
}
