package com.guru.mplayer.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.guru.mplayer.R;

/**
 * Created by Guru on 30-03-2018.
 */

public class CommonHelper {

    public NotificationCompat.Builder notificationBuilder;





    public Notification setNotification(String  channelID, String title, String text, Context context, PendingIntent pendingIntent, Bitmap bitmap)
    {
         notificationBuilder = new NotificationCompat.Builder(context,channelID);

        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mMediaSessionCompat.getSessionToken())
//                .setShowActionsInCompactView(0))
//                .setLargeIcon(mediaMetadata.getBitmap(TAG))
//                .setContentTitle(mediaDescription.getTitle())
//                .setContentText(mediaDescription.getTitle())
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.guitarbg)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setLargeIcon(bitmap)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                // .addAction(R.drawable.play,"play", MediaButtonReceiver.buildMediaButtonPendingIntent(this,))
                //.addAction(R.drawable.play,)
        return  notificationBuilder.build();
    }


}
