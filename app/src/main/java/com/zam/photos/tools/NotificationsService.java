package com.zam.photos.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zam.photos.MainActivity;
import com.zam.photos.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationsService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            Uri icon = remoteMessage.getNotification().getImageUrl();
            // 8 - Show notification after received message
            this.sendVisualNotification(message, icon);
        }
    }

    protected Bitmap doInBackground(Uri imageUrl) {

        InputStream in;
        try {
            URL url = new URL(imageUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---

    private void sendVisualNotification(String messageBody, Uri icon) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
//        Notification notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setLargeIcon(doInBackground(icon))
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setContentIntent(pendingIntent)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
//                        .build();

        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_style);
        RemoteViews expandView = new RemoteViews(getPackageName(), R.layout.notification_expend_long);

        collapseView.setTextViewText(R.id.text_1, messageBody);
        expandView.setTextViewText(R.id.text_expand, messageBody);
        expandView.setImageViewBitmap(R.id.image_expand, doInBackground(icon));

        Notification notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setCustomContentView(collapseView)
                        .setCustomBigContentView(expandView)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .build();

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(2, notificationBuilder);
    }
}