package com.epbit.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epbit.Fcm.CommonUtilities;
import com.epbit.alertspinner.iAm_CustomList;
import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.constants.AppConstants;
import com.epbit.constants.IWebConstant;
import com.epbit.latlong.LatLongDetails;
import com.epbit.login.LoginDetails;
import com.epbit.login.PassengerDetails;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.services.ConfirmRejectCabService;
import com.epbit.services.DriverStatusUpdate;
import com.epbit.services.Driver_loc_update_task;
import com.epbit.services.GetDriverStatus;
import com.epbit.utils.GPSTracker;
import com.epbit.utils.WakeLocker;

public class DriverFragment extends BaseFragment implements View.OnClickListener {
    private Context context;
    boolean doubleBackToExitPressedOnce = false;
    private GPSTracker gpsTracker;
    private LatLongDetails driver_latlongobj = new LatLongDetails();
    private LinearLayout statuslinearlayout, pass_detail_layout, button_layout,
            details_layout, update_profile;
    private TextView driverStatustextview, linear, pass_name, pass_num, pick_loc,
            drop_loc, pick_date, pick_time, pick_time_left, pick_date_left, fare_value;
    private ImageView driverStatusimageview, imageViewPhone;
    public Dialog dialog;
    public ListView list;
    //DriverDetails driver_latlongobj = new DriverDetails();
    ImageButton confirm_driver, reject_driver;
    private boolean First_time_flage;
    private String phoneNumber, first_time_tag = "first_time_flag";
	private UserData userData;
	
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_driver, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.cabbooking_driver);
        try {
            context = getActivity();
			userData = UserData.getinstance(context);
            DriverDetails.setDriverStatus("");
            SharedPreferences myPreferences = context.getSharedPreferences("myprefreferences", 0);
            statuslinearlayout = (LinearLayout) root.findViewById(R.id.driverstatusllid);
            driverStatusimageview = (ImageView) root.findViewById(R.id.driverstatusimgid);
            pass_detail_layout = (LinearLayout) root.findViewById(R.id.passenger_layout_id);
            button_layout = (LinearLayout) root.findViewById(R.id.driver_buttons_linear_id);
            pass_name = (TextView) root.findViewById(R.id.cust_name_dri);
            pass_num = (TextView) root.findViewById(R.id.cust_num_dri);
            pick_loc = (TextView) root.findViewById(R.id.pick_loc_dri);
            drop_loc = (TextView) root.findViewById(R.id.drop_loc_dri);
            pick_time = (TextView) root.findViewById(R.id.pick_time_dri);
            pick_date = (TextView) root.findViewById(R.id.pick_date_dri);
            fare_value = (TextView) root.findViewById(R.id.fare_value);
            pick_date_left = (TextView) root.findViewById(R.id.pick_up_date_tex);
            pick_time_left = (TextView) root.findViewById(R.id.pick_up_time_tex);
            driverStatustextview = (TextView) root.findViewById(R.id.driverstatustextid);
            imageViewPhone = (ImageView) root.findViewById(R.id.imageViewPhone);
            confirm_driver = (ImageButton) root.findViewById(R.id.confirm_img_dri);
            reject_driver = (ImageButton) root.findViewById(R.id.reject_img_dri);
            details_layout = (LinearLayout) root.findViewById(R.id.details_layout);
            update_profile = (LinearLayout) root.findViewById(R.id.updateprofile);
            imageViewPhone.setOnClickListener(this);
            setLinearLayoutOnClickListener(context, statuslinearlayout,
                    driverStatusimageview, driverStatustextview);
            First_time_flage = myPreferences.getBoolean(first_time_tag, true);
            if (First_time_flage) {
                update_profile.setVisibility(View.VISIBLE);
                First_time_flage = false;
                SharedPreferences.Editor editor = myPreferences.edit();
                editor.putBoolean(first_time_tag, First_time_flage);
                editor.commit();
            }
            new GetDriverStatus(context, driverStatusimageview, driverStatustextview, statuslinearlayout).execute();
            confirm_driver.setOnClickListener(this);
            reject_driver.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        get_driver_location();
        return root;
    }

    private LatLongDetails get_driver_location() {
        gpsTracker = new GPSTracker(context);
        LatLongDetails temp_latlongobj = new LatLongDetails();
        if (gpsTracker.canGetLocation()) {
            try {
                temp_latlongobj.driver_latitude = gpsTracker.getLocation().getLatitude();
                temp_latlongobj.driver_longitude = gpsTracker.getLocation().getLongitude();
            } catch (Exception e) {
                Toast.makeText(context, R.string.gps_not_enabled, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
//			gps.showSettingsAlert();
            Log.d("Gps","null in driver activity");
        }
        return temp_latlongobj;
    }

    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
			 * flag 1 to update driver location to the server
			 * flag 2 to show a
			 * message to driver to call its customer for ride later flag 3 to
			 * show that ride later booking is rejected flag 4 to show a message
			 * to call its customer for ride now flag 5 to show that ride now
			 * booking is rejected
			 *
			 */
            String flag = ""
                    + intent.getExtras()
                    .getString(CommonUtilities.EXTRA_MESSAGE).charAt(0);
            String newMessage = intent.getExtras()
                    .getString(CommonUtilities.EXTRA_MESSAGE).substring(1);
          /*  if (flag.contains("1")) {
                // Waking up mobile if it is sleeping
                try {
                    WakeLocker.acquire(context);
                    *//**
                     * Take appropriate action on this message depending upon your
                     * app requirement For now i am just displaying it on the screen
                     * *//*
                   driver_latlongobj = get_driver_location();
                    update_driverlocation(context, newMessage);
                    WakeLocker.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
            if (flag.contains("2")) {
                try {
                    int length = newMessage.split("]").length;
                    String data[] = new String[length];
                    data = newMessage.split("]");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].replace("[", "");
                        Log.e("Data" + i, " " + data[i].toString());
                    }
                    PassengerDetails.passengername = data[0];
                    PassengerDetails.passengernumber = data[1];
                    PassengerDetails.passemger_pick_date = data[2];
                    PassengerDetails.passenger_pick_time = data[3];
                    PassengerDetails.passenger_pick_loc = data[4];
                    PassengerDetails.passenger_drop_loc = data[5];
                    LoginDetails.Unique_Table_ID = data[6];
					userData.setUnique_Table_ID(data[6]);
                    PassengerDetails.fare = data[7];
                    details_layout.setVisibility(View.VISIBLE);
                    pass_detail_layout.setVisibility(View.VISIBLE);
                    button_layout.setVisibility(View.VISIBLE);
                    fare_value.setVisibility(View.VISIBLE);
                    pick_date_left.setVisibility(View.VISIBLE);
                    pick_time_left.setVisibility(View.VISIBLE);
                    pick_date.setVisibility(View.VISIBLE);
                    pick_time.setVisibility(View.VISIBLE);
                    pass_name.setText("" + PassengerDetails.passengername);
                    pass_num.setText("" + PassengerDetails.passengernumber);
                    pick_loc.setText("" + PassengerDetails.passenger_pick_loc);
                    drop_loc.setText("" + PassengerDetails.passenger_drop_loc);
                    pick_date.setText("" + PassengerDetails.passemger_pick_date);
                    pick_time.setText("" + PassengerDetails.passenger_pick_time);
                    fare_value.setText(context.getResources().getString(R.string.currency_sign)+" "+ PassengerDetails.fare);
                    driverStatusimageview.setImageResource(R.drawable.pending);
                    driverStatustextview.setText(R.string.pending_status);
                    DriverDetails.setDriverStatus("" + getResources().getString(R.string.pending_status));
                    new DriverStatusUpdate((Activity) context).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag.contains("3")) {
                try {
                    Toast.makeText(context, "" + newMessage, Toast.LENGTH_SHORT).show();
                    pass_detail_layout.setVisibility(View.INVISIBLE);
                    button_layout.setVisibility(View.INVISIBLE);
                    details_layout.setVisibility(View.INVISIBLE);
                    PassengerDetails.passengername = "";
                    PassengerDetails.passengernumber = "";
                    PassengerDetails.passemger_pick_date = "";
                    PassengerDetails.passenger_pick_time = "";
                    PassengerDetails.passenger_pick_loc = "";
                    PassengerDetails.passenger_drop_loc = "";
                    driverStatusimageview.setImageResource(R.drawable.available);
                    DriverDetails.setDriverStatus("" + getResources().getString(R.string.available_status));
                    driverStatustextview.setText(DriverDetails.getDriverStatus());
                    new DriverStatusUpdate((Activity) context).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag.contains("4")) {
                try {
                    int length = newMessage.split("]").length;
                    String data[] = new String[length];
                    data = newMessage.split("]");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].replace("[", "");
                        Log.e("Data" + i, " " + data[i].toString());
                    }
                    PassengerDetails.passengername = data[0];
                    PassengerDetails.passengernumber = data[1];
                    PassengerDetails.passemger_pick_date = data[2];
                    PassengerDetails.passenger_pick_time = data[3];
                    PassengerDetails.passenger_pick_loc = data[4];
                    PassengerDetails.passenger_drop_loc = data[5];
                    LoginDetails.Unique_Table_ID = data[6];
					userData.setUnique_Table_ID(data[6]);
                    PassengerDetails.fare = data[7];
                    pass_detail_layout.setVisibility(View.VISIBLE);
                    button_layout.setVisibility(View.VISIBLE);
                    pass_name.setText("" + PassengerDetails.passengername);
                    pass_num.setText("" + PassengerDetails.passengernumber);
                    pick_loc.setText("" + PassengerDetails.passenger_pick_loc);
                    drop_loc.setText("" + PassengerDetails.passenger_drop_loc);
                    pick_date_left.setVisibility(View.VISIBLE);
                    pick_time_left.setVisibility(View.VISIBLE);
                    details_layout.setVisibility(View.VISIBLE);
                    fare_value.setVisibility(View.VISIBLE);
                    pick_date.setVisibility(View.VISIBLE);
                    pick_date.setText("" + PassengerDetails.passemger_pick_date);
                    pick_time.setText("" + PassengerDetails.passenger_pick_time);
                    fare_value.setText(context.getResources().getString(R.string.currency_sign)+" "+ PassengerDetails.fare);
                    pick_time.setVisibility(View.VISIBLE);
                    driverStatusimageview.setImageResource(R.drawable.pending);
                    driverStatustextview.setText(R.string.pending_status);
                    DriverDetails.setDriverStatus("" + getResources().getString(R.string.pending));
                    new DriverStatusUpdate((Activity) context).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag.contains("5")) {
                try {
                    Toast.makeText(context, "" + newMessage, Toast.LENGTH_SHORT)
                            .show();
                    pass_detail_layout.setVisibility(View.INVISIBLE);
                    button_layout.setVisibility(View.INVISIBLE);
                    details_layout.setVisibility(View.INVISIBLE);
                    PassengerDetails.passengername = "";
                    PassengerDetails.passengernumber = "";
                    PassengerDetails.passemger_pick_date = "";
                    PassengerDetails.passenger_pick_time = "";
                    PassengerDetails.passenger_pick_loc = "";
                    PassengerDetails.passenger_drop_loc = "";
                    driverStatusimageview.setImageResource(R.drawable.available);
                    DriverDetails.setDriverStatus("" +getResources().getString(R.string.available_status));
                    driverStatustextview.setText(DriverDetails.getDriverStatus());
                    new DriverStatusUpdate((Activity) context).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (flag.contains("7")) {
                try {
                    Toast.makeText(context,""+newMessage,Toast.LENGTH_LONG).show();
               /* driverStatusimageview.setImageResource(R.drawable.booked);
                DriverDetails.setDriverStatus("" + R.string.booked_status);
                driverStatustextview.setText(DriverDetails.getDriverStatus());
                new DriverStatusUpdate((Activity) context).execute();
                replaceFragment(context, new RideDetailsFragment());*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (flag.contains("8")) {
                try {
                    driverStatusimageview.setImageResource(R.drawable.booked);
                    DriverDetails.setDriverStatus("" + R.string.booked_status);
                    driverStatustextview.setText(DriverDetails.getDriverStatus());
                    new DriverStatusUpdate((Activity) context).execute();
                    replaceFragment(context, new RideDetailsFragment());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag.contains("a")) {
                showDialogBox();
            } else {
            }
        }

       /* private void update_driverlocation(Context context, String newMessage) {
            Driver_loc_update_task task = new Driver_loc_update_task(context,
                    newMessage);
            if (newMessage.contains("@")) {
                task.execute();
            }
        }*/
    };

    public void makeCall() {
        try {
            phoneNumber = "tel:" + PassengerDetails.passengernumber;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
            context.startActivity(callIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void showDialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(context.getResources().getString(R.string.cash_payment));
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void backPressed() {
        try {
            if (doubleBackToExitPressedOnce) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    System.exit(0);
                    ((MainActivity) getActivity()).finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    super.onDestroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(context, R.string.tap_again, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
       /* if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }*/
        try {
            context.unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
                    CommonUtilities.DISPLAY_MESSAGE_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_img_dri:
                driverStatustextview.setText(R.string.booked_status);
                driverStatusimageview.setImageResource(R.drawable.booked);
                button_layout.setVisibility(View.INVISIBLE);
                DriverDetails.setDriverStatus("" +getResources().getString(R.string.booked_status));
                new DriverStatusUpdate(context).execute();
                new ConfirmRejectCabService(getActivity(), 1, context)
                        .execute(IWebConstant.CONFIRM);
                break;
            case R.id.reject_img_dri:
                Toast.makeText(context, R.string.please_wait,
                        Toast.LENGTH_SHORT).show();
                details_layout.setVisibility(View.INVISIBLE);
                pass_detail_layout.setVisibility(View.INVISIBLE);
                button_layout.setVisibility(View.INVISIBLE);
                driverStatustextview.setText(R.string.available_status);
                driverStatusimageview.setImageResource(R.drawable.available);
                DriverDetails.setDriverStatus("" + getResources().getString(R.string.available_status));
                new DriverStatusUpdate(context).execute();
                new ConfirmRejectCabService(getActivity(), 1, context).execute(IWebConstant.REJECT);
                break;
            case R.id.imageViewPhone:
                makeCall();
            default:
                break;
        }
    }

    private void setLinearLayoutOnClickListener(final Context context,
                                                LinearLayout statuslinearlayout,
                                                final ImageView driverStatusimageview,
                                                final TextView driverStatustextview) {
        statuslinearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.iamlistspinner);
                dialog.setTitle("I am .....");
                iAm_CustomList listAdapter = new iAm_CustomList(
                        (Activity) context, AppConstants.catgories,
                        AppConstants.imageids);
                list = (ListView) dialog.findViewById(R.id.iam_listview);
                list.setAdapter(listAdapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                driverStatustextview.setText(R.string.available_status);
                                driverStatusimageview.setImageResource(R.drawable.available);
                                DriverDetails.setDriverStatus("" +getResources().getString(R.string.available_status));
                                break;
                            case 1:
                                driverStatustextview.setText(R.string.booked_status);
                                driverStatusimageview.setImageResource(R.drawable.booked);
                                DriverDetails.setDriverStatus("" + getResources().getString(R.string.booked_status));
                                break;
                            case 2:
                                driverStatustextview.setText(R.string.dnd_status);
                                driverStatusimageview.setImageResource(R.drawable.dnd);
                                DriverDetails.setDriverStatus("" + getResources().getString(R.string.dnd_status));
                                break;
                        }
                        new DriverStatusUpdate((Activity) context).execute("");
                        Log.e("Item clicked at ", "" + position);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
