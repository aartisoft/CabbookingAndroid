package com.epbit.constants;

import com.epbit.ccv3.R;

import java.util.ArrayList;

public class AppConstants {
    public static ArrayList<String> categories = new ArrayList<String>();
    public static ArrayList<String> countryname = new ArrayList<String>();
    public static ArrayList<String> countryid = new ArrayList<String>();
    public static String CountryNamesString[] = new String[238];

    public static ArrayList<String> getcategories() {
        categories.clear();
        categories.add("Available");
        categories.add("Booked");
        categories.add("Pending");
        categories.add("DND");
        return categories;
    }

    public static String catgories[] = {"Available", "Booked",
            "DND"};
    public static Integer imageids[] = {R.drawable.available,
            R.drawable.booked, R.drawable.dnd,
            R.drawable.dnd};
}
