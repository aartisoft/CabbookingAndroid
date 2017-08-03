package com.epbit.ccv3;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epbit.constants.IWebConstant;
import com.epbit.helper.CheckConnectivity;
import com.epbit.helper.MyWebViewClient;
import com.epbit.model.UserData;
import com.epbit.services.LoginTask;
import com.epbit.utils.SharedPreferencesUtility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private UserData userdata;
    private EditText username_editext, password_editext;
    private Context context;
    private CheckConnectivity checkConnectivity;
    private SharedPreferencesUtility sharedPreferencesUtility = SharedPreferencesUtility.getInstance();
    private ProgressBar login_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        try {
            userdata = UserData.getinstance(context);
            checkConnectivity = new CheckConnectivity(getApplicationContext());
            username_editext = (EditText) findViewById(R.id.username_edittext);
            password_editext = (EditText) findViewById(R.id.password_edittext);
            login_progress_bar = (ProgressBar) findViewById(R.id.loginprogress);
            login_progress_bar.setVisibility(View.INVISIBLE);
            findViewById(R.id.login_image_button_id).setOnClickListener(this);
            findViewById(R.id.forgot_password_textview_id).setOnClickListener(this);
            findViewById(R.id.Register_textview_id).setOnClickListener(this);
            if (getIntent().getBooleanExtra("EXIT", false)) {
                MyWebViewClient.handleIncomingIntent(context,
                        SplashActivity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void loginProcess() {
        try {
            login_progress_bar.setVisibility(View.INVISIBLE);
            if (checkConnectivity.isNetworkAvailable()) {
                if (username_editext.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            IWebConstant.EMAIL_NOT_ENTERED, Toast.LENGTH_SHORT)
                            .show();
                } else if (username_editext.getText().toString().contains("@") == false
                        || username_editext.getText().toString().contains(".") == false) {
                    Toast.makeText(getApplicationContext(),
                            IWebConstant.EMAIL_NOT_VALID, Toast.LENGTH_SHORT)
                            .show();
                } else if (password_editext.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            IWebConstant.ENTER_PASSWORD, Toast.LENGTH_SHORT).show();
                } else {
                    String userName = username_editext.getText().toString();
                    String password = password_editext.getText().toString();
                    new LoginTask(context, userName, password, login_progress_bar).execute();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.internetdisabledmessage, Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (sharedPreferencesUtility.getLoginflag(context)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
            super.onDestroy();
            finish();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        username_editext.setText("");
        password_editext.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_image_button_id:
                loginProcess();
                break;
            case R.id.forgot_password_textview_id:
                startActivity(new Intent(LoginActivity.this,
                        ForgotPasswordActivity.class));
                break;
            case R.id.Register_textview_id:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }
}
