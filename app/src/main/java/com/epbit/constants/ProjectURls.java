package com.epbit.constants;

public class ProjectURls {
    //public static String BASE_URL = "http://www.epbitservices.com/demo/cabbooking-cc-v3/ws/";
    public static String BASE_URL = "http://products4demo.in/CabBookingApp/Cabbooking/ws/";

    public static String REGISTER_URL = BASE_URL + "user_register.php";
    public static String LOGIN_URL_STRING = BASE_URL + "checklogin.php";
    public static String FORGOT_PASSWORD_URL = BASE_URL + "forgot_password.php";
    // Side Menu Urls
    public static String PROFILE_ACTIVITY_URL = BASE_URL + "user_profile.php";
    public static String CAB_MONEY_URL = BASE_URL + "cab_money.php?email=";
    public static String RATE_CARD_PROJECT_URL = BASE_URL + "rate_card.php";
    public static String HELP_PROJECT_URL = BASE_URL + "help.php";
    public static String REFER_FRIEND_URL = BASE_URL + "refer_count.php";
    public static String RIDES_Driver_URL = BASE_URL
            + "driver_rides.php?email=";
    public static String RIDES_Passenger_URL = BASE_URL
            + "passenger_rides.php?email=";
    // Gcm Registration URL
    public static final String SERVER_URL = BASE_URL + "gcm_register.php";
    // update driver
    public static final String GET_DRIVER_CURRENT_LOCATION = BASE_URL
            + "track_driver.php";
    // Driver Urls
    public static String GET_DRIVER_STATUS_URL = BASE_URL
            + "get_driver_status.php";
    public static String DRIVER_STATUS_CHANGE_URL = BASE_URL
            + "update_driver_status.php";
    public static String DRIVER_UPDATE_LOC_URL = BASE_URL
            + "update_driver_lat&long.php";
    // Current RIDE Booking URLS
    public static String RIDE_LATER_CONFIRM_URL = BASE_URL + "ride_later.php";
    public static String RIDE_NOW_CONFIRM_URL = BASE_URL + "ride_now.php";
    public static String UPDATE_USER_LOCATION_URL = BASE_URL
            + "cal_distance.php";
   public static final String UPDATE_PROFILE_PIC_URL ="upload_user_pic.php";
    public static String RIDES_PROJECT_URL = BASE_URL + "rides.php?email=";
    public static String CONFIRM_REJECT_URL = BASE_URL
            + "ride_later_confirm.php";
    public static String CONFIRM_REJECT_Now_URL = BASE_URL
            + "ride_now_confirm.php";
    public static String Update_Payment_Details_URL = BASE_URL
            + "payment_response.php";
    public static String Driver_RIDE_CONFIRM_URL = "";
    public static String NEAREST_CAB_URL = BASE_URL + "send_notification.php";

    public static String getProfileUrl(String username, String user_type) {
        return PROFILE_ACTIVITY_URL + "?email=" + username + "&user_type="
                + user_type.toLowerCase();
    }

    public static String TRACK_DRIVER_URL = BASE_URL
            + "user_track_driver.php?driver_id=";
    public static String PAYMENT_SUCCESS_URL = BASE_URL
            + "payment-successful.htm";
    public static String PAYMENT_FAIL_URL = BASE_URL + "payment-fail.htm";
}