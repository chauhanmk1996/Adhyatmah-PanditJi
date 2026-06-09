package com.app.panditji.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.panditji.R
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import kotlin.getValue

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val prefs by inject<PrefsHelper>()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("TAG", "sendNotification: $remoteMessage remote")

        // Handle foreground notification
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the token to your server if needed
        Log.d("FCM", "FCMToken: $token")
        prefs.fcmToken = token
    }
    private fun sendNotification(title: String?, messageBody: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"

        // Create channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // This will simply open the app like tapping the app icon
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.app_logo)
            .setContentTitle(title ?: "New Notification")
            .setContentText(messageBody ?: "")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
