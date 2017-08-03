package com.epbit.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epbit.ccv3.R;
import com.epbit.constants.IWebConstant;
import com.epbit.helper.CheckConnectivity;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.services.ConfirmRejectCabService;
import com.epbit.services.RideNowConfirmService;
import com.epbit.services.Update_user_loc_task;

public class RideFragment extends BaseFragment implements View.OnClickListener {
    private ImageButton ride_later, ride_now, confirm, reject;
    private TextView source, cab_type, destination, distance, dis_time, cab_time, travelTime,
            driver_name, contact, cab_num, fare, progress_text_view,fareApprox;
    private LinearLayout getting_cabdetailsLL, got_cabDetailsLL, ridenowlaterlayout,fareDistanceRideLayout;
    private ProgressBar progress;
    private Context context;
    private CheckConnectivity checkConnectivity;
    public static boolean flag = false;
    private UserData userData;
    private String phoneNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ride, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ride");
        try {
            context = getActivity();
            userData = UserData.getinstance(context);
            getting_cabdetailsLL = (LinearLayout) root.findViewById(R.id.gettingdetailsLL);
            got_cabDetailsLL = (LinearLayout) root.findViewById(R.id.cab_details_layout);
            fareDistanceRideLayout = (LinearLayout) root.findViewById(R.id.distance_fare_layout_Ride);
            cab_time = (TextView) root.findViewById(R.id.cab_reach_time_Ride);
            driver_name = (TextView) root.findViewById(R.id.driver_name_Ride);
            contact = (TextView) root.findViewById(R.id.driver_contact_num_Ride);
            cab_num = (TextView) root.findViewById(R.id.Cab_num_Ride);
            progress_text_view = (TextView) root.findViewById(R.id.cab_details_progress_textview);
            progress = (ProgressBar) root.findViewById(R.id.cab_details_progress);
            ride_now = (ImageButton) root.findViewById(R.id.ride_now_IMGbutton);
            confirm = (ImageButton) root.findViewById(R.id.confirm_IMGbutton);
            reject = (ImageButton) root.findViewById(R.id.reject_IB_Ride);
            ridenowlaterlayout = (LinearLayout) root.findViewById(R.id.ridenowlaterbuttonlayout);
            ride_later = (ImageButton) root.findViewById(R.id.ride_later_IB_Ride);
            source = (TextView) root.findViewById(R.id.current_loc_Ride);
            cab_type = (TextView) root.findViewById(R.id.cab_type_Ride);
            destination = (TextView) root.findViewById(R.id.destination_Ride);
            distance = (TextView) root.findViewById(R.id.distance_Ride);
            fare = (TextView) root.findViewById(R.id.fare_Ride);
            fareApprox=(TextView)root.findViewById(R.id.fare_approx);
            travelTime = (TextView) root.findViewById(R.id.travelTime);
            checkConnectivity = new CheckConnectivity(context);
            ride_later.setOnClickListener(this);
            ride_now.setOnClickListener(this);
            confirm.setOnClickListener(this);
            reject.setOnClickListener(this);
            contact.setOnClickListener(this);
            new Update_user_loc_task(context, ridenowlaterlayout, RideFragment.this).execute();
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    public void setData() {
        try {
            source.setText(userData.getAddress());
            if (userData.getCab_type().equalsIgnoreCase("1")) {
               // fare.setText("$75");
                cab_type.setText(R.string.cab1);
            } else if (userData.getCab_type().equalsIgnoreCase("2")) {
               // fare.setText("$100");
                cab_type.setText(R.string.cab2);
            } else {
                //fare.setText("$250");
                cab_type.setText(R.string.cab3);
            }
            destination.setText(userData.getDesti_address());
            distance.setText(userData.getDistance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void backPressed() {
        replaceFragment(context, new SelectDestinationFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ride_later_IB_Ride:
                replaceFragment(context, new RideLaterFragment());
                break;
            case R.id.ride_now_IMGbutton:
                if (checkConnectivity.isNetworkAvailable()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getResources().getString(
                            R.string.messagedialogtitle))
                            .setMessage(getResources().getString(
                                    R.string.messagedialogmessage))
                            .setCancelable(false)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new RideNowConfirmService(context).execute();
                                    ride_now.setVisibility(View.GONE);
                                    ride_later.setVisibility(View.GONE);
                                    confirm.setVisibility(View.VISIBLE);
                                    reject.setVisibility(View.VISIBLE);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.confirm_IMGbutton:
                try {
                    if (checkConnectivity.isNetworkAvailable()) {
                        Toast.makeText(context, R.string.confirming_msg,
                                Toast.LENGTH_LONG).show();
                        new ConfirmRejectCabService(getActivity(), 1,
                                context).execute(IWebConstant.CONFIRM);
                        confirm.setVisibility(View.INVISIBLE);
                        reject.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.reject_IB_Ride:
                try {
                    if (checkConnectivity.isNetworkAvailable()) {
                        Toast.makeText(context, R.string.rejecting_msg,
                                Toast.LENGTH_LONG).show();
                        new ConfirmRejectCabService(getActivity(), 2, context).execute(IWebConstant.REJECT);
                        confirm.setVisibility(View.INVISIBLE);
                        reject.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.driver_contact_num_Ride:
                makeCall();
                break;

            default:
                break;
        }
    }

    private void makeCall() {
        try {
            phoneNumber = "tel:" + DriverDetails.getDriverNumber();
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
            context.startActivity(callIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void responseFirst() {
        try {
            progress_text_view.setText(R.string.no_cabs);
            progress.setVisibility(View.INVISIBLE);
            RideFragment.flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void responseSecond() {
        try {
            getting_cabdetailsLL.setVisibility(View.GONE);
            got_cabDetailsLL.setVisibility(View.VISIBLE);
            ridenowlaterlayout.setVisibility(View.VISIBLE);
            fareDistanceRideLayout.setVisibility(View.VISIBLE);
            cab_time.setText("" + DriverDetails.getNearesCabReachingTime());
            driver_name.setText("" + DriverDetails.getDriverName());
            contact.setText("" + DriverDetails.getDriverNumber());
            cab_num.setText("" + DriverDetails.getCabNumber());
            fare.setText(getResources().getString(R.string.currency_sign)+" "+DriverDetails.getFarePerUnit());
            travelTime.setText(DriverDetails.getTravelTime());
            fareApprox.setText(getResources().getString(R.string.currency_sign)+" "+DriverDetails.getFare());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
