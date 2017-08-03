package com.epbit.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.epbit.Fcm.CommonUtilities;
import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.helper.CheckConnectivity;
import com.epbit.model.DriverDetails;
import com.epbit.model.UserData;
import com.epbit.services.NearestCabAsyntask;
import com.epbit.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends BaseFragment implements View.OnClickListener, LocationListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLoadedCallback {
    private Context context;
    private GoogleMap googleMap;
    private ImageButton selectDestinationButton, mapRefreshButton, yellowCab, limozine, personalCab;
    private CheckConnectivity checkConnectivity;
    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog dialog;
    private static boolean isGPSEnabled, isNetworkEnabled;
    private LatLng center = null;
    private LatLng latLng;
    boolean gooleMapFlag = true;
    private String address;
    UserData userdata;
    private GPSTracker gpsTracker;
    final int BACK_DIALOG = 1;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 5;
    private final int MY_PERMISSIONS_REQUEST_STOP = 6;
    private static LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        context = getActivity();
        try {
            userdata = UserData.getinstance(context);
            checkConnectivity = new CheckConnectivity(context);
            selectDestinationButton = (ImageButton) root.findViewById(R.id.select_destination_button);
            selectDestinationButton.setVisibility(View.GONE);
            mapRefreshButton = (ImageButton) root.findViewById(R.id.refresh_map);
            personalCab = (ImageButton) root.findViewById(R.id.personalcabmapbutton);
            yellowCab = (ImageButton) root.findViewById(R.id.yellowcabmapbutton);
            limozine = (ImageButton) root.findViewById(R.id.limosinemapbutton);
            gpsTracker = new GPSTracker(context);
            mapRefreshButton.setOnClickListener(this);
            selectDestinationButton.setOnClickListener(this);
            yellowCab.setOnClickListener(this);
            limozine.setOnClickListener(this);
            personalCab.setOnClickListener(this);
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            else
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                initialiseMap();
            }
            context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
                    CommonUtilities.DISPLAY_MESSAGE_ACTION));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return root;
    }

    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
			 * flag 1 to update driver location to the server flag 2 to show a
			 * message to driver to call its customer
			 */
            String flag = ""
                    + intent.getExtras()
                    .getString(CommonUtilities.EXTRA_MESSAGE).charAt(0);
            String newMessage = intent.getExtras()
                    .getString(CommonUtilities.EXTRA_MESSAGE).substring(1);
        }
    };

    public String getAddress(String lat, String lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            " ");
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }

    private void initialiseMap() {
        if (checkConnectivity.isNetworkAvailable()) {
            if (dialog == null)
                dialog = ProgressDialog.show(context, "",
                        getString(R.string.please_wait), true, true);
            try {
                Location location;
                if (googleMap == null) {
                    googleMap = ((SupportMapFragment) getChildFragmentManager()
                            .findFragmentById(R.id.home_map_fragment)).getMap();
                }
                if (googleMap != null) {
                    location = gpsTracker.getLocation();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    // googleMap.setPadding(0, 0, 300, 405);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 12));
                    //googleMap.setOnCameraChangeListener(this);
                    center = new LatLng(location.getLatitude(), location.getLongitude());
                    placeMarker(center);
                    changeCamera();
                }
            } catch (Exception w) {
                w.printStackTrace();
            }
        } else {
            Toast.makeText(context, R.string.internetdisabledmessage, Toast.LENGTH_LONG).show();
        }
    }

    public void placeMarker(LatLng latLng) {
        try {
            if (latLng != null) {
                String address = getAddress("" + latLng.latitude, "" + latLng.longitude);
                this.latLng = latLng;
                googleMap.setMyLocationEnabled(false);
                //googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(latLng.latitude,
                                        latLng.longitude))
                        .title(address)
                        /*.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker))*/
                );
                marker.showInfoWindow();
                userdata.setAddress(address);
                userdata.setSourc_lat(latLng.latitude);
                userdata.setSourc_longt(latLng.longitude);
                new NearestCabAsyntask(context, googleMap).execute();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void mapLoaded() {
        if (googleMap != null) {
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    selectDestinationButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void changeCamera() {
        if (googleMap != null)
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    googleMap.clear();
                    center = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    placeMarker(center);
                }
            });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                center = new LatLng(latLng.latitude, latLng.longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 12));
                placeMarker(center);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialiseMap();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    protected void backPressed() {
        //replaceFragment(context,new HelpFragment());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_destination_button:
                String sourceAddress = userdata.getAddress();
                if (DriverDetails.getNoCabFound().equalsIgnoreCase("NoCabFound")) {

                    Toast.makeText(context, "No Cab Available Right Now", Toast.LENGTH_SHORT).show();

                }
                else{
                    if (!sourceAddress.equalsIgnoreCase("Can't get Address!") && !sourceAddress.equalsIgnoreCase(" ")) {
                        replaceFragment(context, new SelectDestinationFragment());
                    } else {
                        Toast.makeText(context, "First Select Source Address", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
                break;
            case R.id.refresh_map:
                if (googleMap != null) {
                    try {
                        Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        googleMap.clear();
                        initialiseMap();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.yellowcabmapbutton:
                userdata.setCab_type("1");
                new NearestCabAsyntask(context, googleMap).execute();
                yellowCab.setImageDrawable(getResources().getDrawable(
                        R.drawable.yellow_cab_clicked));
                personalCab.setImageDrawable(getResources().getDrawable(
                        R.drawable.personal_cab));
                limozine.setImageDrawable(getResources().getDrawable(
                        R.drawable.limousine));
                break;
            case R.id.personalcabmapbutton:
                userdata.setCab_type("2");
                new NearestCabAsyntask(context, googleMap).execute();
                yellowCab.setImageDrawable(getResources().getDrawable(
                        R.drawable.yellow_cab));
                personalCab.setImageDrawable(getResources().getDrawable(
                        R.drawable.personal_cab_clicked));
                limozine.setImageDrawable(getResources().getDrawable(
                        R.drawable.limousine));
                break;
            case R.id.limosinemapbutton:
                userdata.setCab_type("3");
                new NearestCabAsyntask(context, googleMap).execute();
                yellowCab.setImageDrawable(getResources().getDrawable(R.drawable.yellow_cab));
                personalCab.setImageDrawable(getResources().getDrawable(
                        R.drawable.personal_cab));
                limozine.setImageDrawable(getResources().getDrawable(
                        R.drawable.limousine_clicked));
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            System.gc();
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_STOP);
        } else {
            locationManager.removeUpdates(this);
        }
        try {
            context.unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.home_map_fragment));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mapLoaded();
        } catch (Exception e) {
            Toast.makeText(context, R.string.map_expception,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}