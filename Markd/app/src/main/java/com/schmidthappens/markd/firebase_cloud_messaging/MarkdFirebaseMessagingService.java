package com.schmidthappens.markd.firebase_cloud_messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.NotificationsActivity;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by joshua.schmidtibm.com on 11/24/17.
 */

public class MarkdFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingSvc";
    private static int badgeCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        ShortcutBadger.applyCount(MarkdFirebaseMessagingService.this, ++badgeCount);
        Log.d(TAG, "BadgeCount - " + badgeCount);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    @SuppressWarnings("deprecation")
    private void sendNotification(String messageBody) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent intentToOpenNotificationActivity = createNotificationActivityPendingIntent();

        if(Build.VERSION.SDK_INT >= 26 && notificationManager != null) {
            if(notificationManager.getNotificationChannel("contractorChannel") == null) {
                createNotificationChannel();
            }
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "contractorChannel")
                            .setSmallIcon(R.drawable.ic_contractor_notification)
                            .setContentTitle("Contractor Notification")
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentIntent(intentToOpenNotificationActivity);
            Log.d(TAG, "Notifying manager");
            notificationManager.notify(0, notificationBuilder.build());
        } else if(Build.VERSION.SDK_INT < 26 && notificationManager != null) {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_contractor_notification)
                            .setContentTitle("Contractor Notification")
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentIntent(intentToOpenNotificationActivity);

            notificationManager.notify(0, notificationBuilder.build());
        } else {
            Log.d(TAG, "notificationManager null");
        }
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26 && notificationManager != null) {
            String channelId = "contractorChannel";
            CharSequence channelName = "Notifications sent from contractors";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private PendingIntent createNotificationActivityPendingIntent() {
        Intent resultIntent = new Intent(this, NotificationsActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        return PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
    }
    public static boolean hasNotifications() {
        return (badgeCount > 0);
    }
    public static void resetBadgeCount(Context context) {
        Log.d(TAG, "BadgeCount - " + badgeCount);
        badgeCount = 0;
        ShortcutBadger.applyCount(context, badgeCount);
    }
}
