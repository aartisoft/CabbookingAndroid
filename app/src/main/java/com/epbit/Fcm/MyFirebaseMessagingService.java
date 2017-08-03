package com.epbit.Fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.epbit.ccv3.MainActivity;
import com.epbit.ccv3.R;
import com.epbit.services.Driver_loc_update_task;
import com.epbit.utils.WakeLocker;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // public static String message;
    private static final String TAG = "MyFirebaseMsgService";
    String message1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //*if (remoteMessage.getNotification().getBody()!=null) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> message = remoteMessage.getData();
        //String string=remoteMessage.getData().get("title"),remoteMessage.getData().get("body");
        Log.d(TAG, "Notification Message Body: " + message);
        //Calling method to generate notification
        NotificationFilter.processMyNoti(getApplicationContext(), message);

        if (message.containsKey("noti_data")) {
            message1 = message.get("noti_data");
            //flag = "" + 1;
            try {
                WakeLocker.acquire(getApplicationContext());
                /**
                 * Take appropriate action on this message depending upon your
                 * app requirement For now i am just displaying it on the screen
                 * */
                //driver_latlongobj = get_driver_location();
                update_driverlocation(getApplicationContext(), message1);
                WakeLocker.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       /* if (message.containsKey("driver_payment_msg")) {
            message1 = message.get("driver_payment_msg");

            Toast.makeText(getApplicationContext(),""+message1,Toast.LENGTH_LONG).show();
        }
*/
        //sendNotification(message);
    }


    private void update_driverlocation(Context context, String newMessage) {
        Driver_loc_update_task task = new Driver_loc_update_task(context,
                newMessage);
        if (newMessage.contains("@")) {
            task.execute();
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.notificationicon)
                .setContentTitle("Cabbooking Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
