package com.epbit.ccv3;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epbit.fragments.CabMoneyFragment;
import com.epbit.fragments.DriverFragment;
import com.epbit.fragments.HelpFragment;
import com.epbit.fragments.HomeFragment;
import com.epbit.fragments.ProfileFragment;
import com.epbit.fragments.RateCardFragment;
import com.epbit.fragments.RideDetailsFragment;
import com.epbit.services.FcmTokenToServer;
import com.epbit.utils.SharedPreferencesUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private NavigationView navigationView;
    private TextView userName, userEmailId;
    private ImageView profilePic;
    private String base64String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            context = MainActivity.this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setItemIconTintList(null);
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            userName = (TextView) headerView.findViewById(R.id.user_name);
            userEmailId = (TextView) headerView.findViewById(R.id.user_emailid);
            profilePic = (ImageView) headerView.findViewById(R.id.profile_picture);
            base64String = SharedPreferencesUtility.loadProfilePic(context);
            if (base64String.isEmpty()) {
                profilePic.setImageDrawable(getResources().getDrawable(R.drawable.user));
            } else {
               /* byte[] encodeByte = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profilePic.setImageBitmap(bitmap);*/
                profilePic.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }
            userName.setText(SharedPreferencesUtility.loadUsername(context));
            userEmailId.setText(SharedPreferencesUtility.loadUserType(context));
            //Checking play service is available or not
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
            //if play service is not available
            if (ConnectionResult.SUCCESS != resultCode) {
                //If play service is supported but not installed
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    //Displaying message that play service is not installed
                    Toast.makeText(context, R.string.playstore_not_install, Toast.LENGTH_LONG).show();
                    GooglePlayServicesUtil.showErrorNotification(resultCode, context);
                    //If play service is not supported
                    //Displaying an error message
                } else {
                    Toast.makeText(context, R.string.playstore_not_support, Toast.LENGTH_LONG).show();
                }
                //If play service is available
            } else {
                //Starting intent to register device
                /*Intent itent = new Intent(this, GCMRegistrationIntentService.class);
                startService(itent);*/
            }
            // onNewIntent(getIntent());
            new FcmTokenToServer(context).execute();
            if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("passenger")) {
                Fragment fragment;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new HomeFragment();
                fragmentTransaction.replace(R.id.containerView, fragment, null);
                fragmentTransaction.commit();
            } else {
                Fragment fragment;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new DriverFragment();
                fragmentTransaction.replace(R.id.containerView, fragment, null);
                fragmentTransaction.commit();
                MenuItem item = navigationView.getMenu().getItem(4);
                item.setVisible(false);
                MenuItem item2 = navigationView.getMenu().getItem(3);
                item2.setTitle(getResources().getString(R.string.item_three));
                item2.setIcon(R.drawable.cabmoney);
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
    }

    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.home:
                if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("passenger")) {
                    fragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.containerView, fragment, null);
                    fragmentTransaction.commit();
                } else {
                    fragment = new DriverFragment();
                    fragmentTransaction.replace(R.id.containerView, fragment, null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.containerView, fragment, null);
                fragmentTransaction.commit();
                break;
            case R.id.rides:
                fragment = new RideDetailsFragment();
                fragmentTransaction.replace(R.id.containerView, fragment, null);
                fragmentTransaction.commit();
                break;
            case R.id.rate_card:
                if (SharedPreferencesUtility.loadUserType(context).equalsIgnoreCase("passenger")) {
                    fragment = new RateCardFragment();
                    fragmentTransaction.replace(R.id.containerView, fragment, null);
                    fragmentTransaction.commit();
                } else {
                    fragment = new CabMoneyFragment();
                    fragmentTransaction.replace(R.id.containerView, fragment, null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.help:
                fragment = new HelpFragment();
                fragmentTransaction.replace(R.id.containerView, fragment, null);
                fragmentTransaction.commit();
                break;
            case R.id.sign_out:
                signOutMethod();
                break;
            case R.id.refer_friend:
                referFriendsMethod();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void referFriendsMethod() {
        Resources resources = getResources();
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it
        // doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
        emailIntent.setType("message/rfc822");
        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent,
                "Share");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a
            // LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter")
                    || packageName.contains("facebook")
                    || packageName.contains("mms")
                    || packageName.contains("com.whatsapp")
                    || packageName.contains("com.linkedin.android")
                    || packageName.contains("com.viber.voip")
                    || packageName.contains("com.skype.raider")
                    || packageName.contains("com.google.android.talk")
                    || packageName.contains("tencent.mm")
                    || packageName
                    .contains("com.tencent.mm.ui.tools.ShareToTimeLineUI")
                    || packageName.contains("com.evernote")
                    || packageName.contains("com.bsb.hike")
                    || packageName
                    .contains("com.google.android.apps.babel")
                    || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName,
                        ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intentList.add(new LabeledIntent(intent, packageName, ri
                        .loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList
                .toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void signOutMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to signout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SharedPreferencesUtility
                                    .resetSharedPreferences(context);
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(new Intent(context, LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
