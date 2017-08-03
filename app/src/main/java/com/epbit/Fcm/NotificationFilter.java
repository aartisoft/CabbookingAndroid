package com.epbit.Fcm;

import android.content.Context;

import com.epbit.services.UpdateDriverLocationService;

import java.util.Map;

public class NotificationFilter {
    /*
     * @params flag
     *
     * 1 : notification for updating location
     * 2 : notification to call customer
     * 3 : notification on Booking rejected
     * 4 : notification to call customer in case of ride now
     * 5 : notification on booking rejected in case of ride now
     * 6 : notification for payment confirmation to user
     * 7 : notification for payment confirmation to driver
     * 8 : notification to inform driver that your ride has been arrived
     * 9 : notification to inform Passenger that your ride has been arrived
     */
    static String flag = "";
    private static String message;
    private static String[] msg = new String[3];
    static String message1;

    public static void processMyNoti(Context context, Map<String, String> message) {
        // use GCM INTENT Service to display message in notification bar
        flag = "";
        if (message.containsKey("noti_data")) {
            message1 = message.get("noti_data");
            flag = "" + 1;
        }

        //book ride later confirm
        if (message.containsKey("ride_confirm_msg")) {
            message1 = message.get("ride_confirm_msg");
            flag = "" + 2;
        }
        if (message.containsKey("ride_reject_msg")) {
            message1 = message.get("ride_reject_msg");
            flag = "" + 3;
        }

        //book ride now confirm
        if (message.containsKey("ride_now_confirm_msg")) {
            message1 = message.get("ride_now_confirm_msg");
            flag = "" + 4;
        }
        if (message.containsKey("ride_now_reject_msg")) {
            message1 = message.get("ride_now_reject_msg");
            flag = "" + 5;
        }
        if (message.containsKey("passenger_payment_msg")) {
            message1 = message.get("passenger_payment_msg");
            flag = "" + 6;
        }
        if (message.containsKey("driver_payment_msg")) {
            message1 = message.get("driver_payment_msg");
            flag = "" + 7;
        }
        if (message.containsKey("inform_driver_msg")) {
            message1 = message.get("inform_driver_msg");
            flag = "" + 8;
        }
        if (message.containsKey("inform_passenger_msg")) {
            message1 = message.get("inform_passenger_msg");
            flag = "" + 9;
        }
        if (message.containsKey("update_key")) {
            message1 = message.get("update_key");
            msg = message.get("update_key").split("'");
            new UpdateDriverLocationService(context).execute(msg[2]);
        }
        if (message.containsKey("payment")) {
            message1 = message.get("payment_method");
            flag = "" + "a";
        }
        CommonUtilities.displayMessage(context, flag + message1);
        // notifies user
        //GCMIntentService.generateNotification(context, flag + message1);
    }
}