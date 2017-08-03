package com.epbit.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.epbit.ccv3.R;
import com.epbit.model.UserData;
import com.epbit.services.DrawrootTask;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectDestinationFragment extends BaseFragment implements View.OnClickListener {
    private Context context;
    private ImageButton bookNowButton;
    private TextView set_source_SD, destinationText_SD;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;
    private UserData userData;
    private GoogleMap googleMap;
    private LatLng mapCenter;
    private LatLng location;
    private Double sourceLat, sourceLong;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_select_destination, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.item_select_destination);
        try {
            context = getActivity();
            userData = UserData.getinstance(context);
            destinationText_SD = (TextView) root.findViewById(R.id.destination_SD);
            bookNowButton = (ImageButton) root.findViewById(R.id.book_now_Imagebutton_SD);
            set_source_SD = (TextView) root.findViewById(R.id.source_textview_SD);
            set_source_SD.setText(userData.getAddress());
            sourceLat = userData.getSourc_lat();
            sourceLong = userData.getSourc_longt();
            userData.setDesti_address("");
            setClickListner();
            initialisemap();
            onMapClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        return root;
    }

    private void onMapClick() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    googleMap.clear();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 12));
                    String address = getAddress("" + latLng.latitude, "" + latLng.longitude);
                    destinationText_SD.setText(address);
                    userData.setDesti_address(address);
                    userData.setDest_lat(latLng.latitude);
                    userData.setDest_longt(latLng.longitude);
                    drawRootFromSourcToDestination(new LatLng(sourceLat, sourceLong), latLng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setClickListner() {
        try {
            bookNowButton.setOnClickListener(this);
            destinationText_SD.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialisemap() {
        try {
            googleMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.mapfragment_SD)).getMap();
            if (googleMap != null) {
                location = new LatLng(sourceLat, sourceLong);
                mapCenter = location;
                if (location != null) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(userData.getAddress())
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.source)));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            mapCenter, 12));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawRootFromSourcToDestination(LatLng source, LatLng destination) {
        try {
            googleMap.clear();
            String lat, longt;
            placeMarker(source, "1");
            placeMarker(destination, "2");
            DrawrootTask drawrootTask = new DrawrootTask(context, source,
                    destination, googleMap,bookNowButton);
            drawrootTask.execute();
            String address = "";
            lat = "" + destination.latitude;
            longt = "" + destination.longitude;
            userData.setDest_lat(destination.latitude);
            userData.setDest_longt(destination.longitude);
            address = getAddress(lat, longt);
            userData.setDesti_address(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void placeMarker(LatLng latlong, String str) {
        try {
            if (str.equalsIgnoreCase("1"))
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latlong.latitude, latlong.longitude))
                        .snippet(String.valueOf(""))
                        .draggable(true)
                        .title(userData.getAddress())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.source)));
            else
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latlong.latitude, latlong.longitude))
                        .snippet(String.valueOf(""))
                        .draggable(true)
                        .title(userData.getDesti_address())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.destination)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void backPressed() {
        replaceFragment(context, new HomeFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.destination_SD:
                callPlaceAutocompleteActivityIntent();
                break;
            case R.id.book_now_Imagebutton_SD:
                bookNow();
                break;
        }
    }

    private void bookNow() {
        try {
            if (!userData.getDesti_address().equalsIgnoreCase("")) {
                String string = userData.getDistance();
                String distance = string.replaceAll("[km m]", "").replaceAll("m", "");
                if (Double.parseDouble(distance) < 2) {
                    Toast.makeText(context, R.string.short_distance, Toast.LENGTH_SHORT).show();
                } else {
                    replaceFragment(context, new RideFragment());
                }
            } else {
                Toast.makeText(context,
                        R.string.select_destination_error,
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException | Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build((Activity) context);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.mapfragment_SD));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {                                                                                // Load the images from gallery and camera
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(context, data);
                    destinationText_SD.setText(place.getAddress());
                    LatLng latLng = place.getLatLng();
                    userData.setDesti_address("" + place.getAddress());
                    userData.setDest_lat(latLng.latitude);
                    userData.setDest_longt(latLng.longitude);
                    drawRootFromSourcToDestination(new LatLng(sourceLat, sourceLong), latLng);
                    Log.i("tag", "Place:" + place.toString());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(context, data);
                    Log.i("tag", status.getStatusMessage());
                } else if (requestCode == RESULT_CANCELED) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", "" + e);
        }
    }
}
