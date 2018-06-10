package com.catalin.mymedic.component

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.catalin.mymedic.R
import com.catalin.mymedic.data.AppointmentNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * @author catalinradoiu
 * @since 6/8/2018
 */
class MyMedicFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @SuppressLint("NewApi")
    override fun onMessageReceived(message: RemoteMessage?) {
        val a = message
        Log.d("firebaseMessaging", "messageReceivedx`")

        @Suppress("DEPRECATION")
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("channel_001", "FirebaseChannel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(this, notificationChannel.id)
        } else {
            NotificationCompat.Builder(this)
        }
        val notificationData = AppointmentNotification.createNotification(message?.data ?: HashMap())
        val notification = notificationBuilder.setSmallIcon(R.drawable.ic_application_logo)
            .setContentTitle(notificationData.title)
            .setAutoCancel(true)
            .setContentText(notificationData.message)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.message))
            .build()

        notificationManager.notify(notification.hashCode(), notification)

    }

    companion object {
        private const val NEW_APPOINTMENT_MESSAGE = 1
        private const val APPOINTMENT_APPROVED_MESSAGE = 2
    }
}