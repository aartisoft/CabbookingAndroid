package com.epbit.ccv3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.epbit.constants.ProjectURls;
import com.epbit.helper.MyWebViewClient;

public class RegisterActivity extends AppCompatActivity {
    WebView register_web_view;
    Context context;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = RegisterActivity.this;
        try {
            register_web_view = (WebView) findViewById(R.id.register_webview);
            MyWebViewClient.enableWebViewSettings(register_web_view);
            register_web_view.setWebChromeClient(MyWebViewClient.getWebChromeInstance());
            register_web_view.setWebViewClient(new MyWebViewClient(this));
            register_web_view.addJavascriptInterface(new DemoJavaScriptInterface(
                    this), "android");
            register_web_view.loadUrl(ProjectURls.REGISTER_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface(Context c) {
        }

        // This Function is Triggered when user Click on Already Register Link
        // of Web Page
        @JavascriptInterface
        public void clickOnAlreadyRegister() {
            mHandler.post(new Runnable() {
                public void run() {
                    startActivity(new Intent(RegisterActivity.this,
                            LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        }

        // This Function is Triggered when user Click on Register Link of Web
        // Page
        // TODO Auto-generated constructor stub
        @JavascriptInterface
        public void clickOnRegister() {
            mHandler.post(new Runnable() {
                public void run() {
                    try {
                        Toast.makeText(getApplicationContext(),
                                R.string.successfully_registered,
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,
                                LoginActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
