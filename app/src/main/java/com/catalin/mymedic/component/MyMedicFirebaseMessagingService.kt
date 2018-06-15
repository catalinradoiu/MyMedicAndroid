package com.catalin.mymedic.component

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.catalin.mymedic.R
import com.catalin.mymedic.data.AppointmentNotification
import com.catalin.mymedic.feature.launcher.LauncherActivity
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
        message?.let {
            @Suppress("DEPRECATION")
            val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(MY_MEDIC_NOTIFICATIONS_CHANNEL_ID, MY_MEDIC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
                NotificationCompat.Builder(this, notificationChannel.id)
            } else {
                NotificationCompat.Builder(this)
            }


            val messageType = it.data[NOTIFICATION_TYPE_KEY]?.toInt() ?: 0
            val intentBuilder = createTaskStackBuilder(messageType)
            val notificationData = AppointmentNotification.createNotification(it.data ?: HashMap())
            val notification = buildNotification(notificationBuilder, notificationData, intentBuilder)

            notificationManager.notify(notification.hashCode(), notification)
        }
    }

    private fun buildNotification(notificationBuilder: NotificationCompat.Builder, notificationData: AppointmentNotification, intentBuilder: TaskStackBuilder) =
        notificationBuilder.setSmallIcon(R.drawable.ic_application_logo)
            .setContentTitle(notificationData.title)
            .setAutoCancel(true)
            .setContentText(notificationData.message)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.message))
            .setContentIntent(intentBuilder.getPendingIntent(INTENT_BUILDER_REQUEST_CODE, PendingIntent.FLAG_CANCEL_CURRENT))
            .build()

    private fun createTaskStackBuilder(appointmentMessageType: Int) = when (appointmentMessageType) {
        NEW_APPOINTMENT_MESSAGE -> TaskStackBuilder.create(this).addNextIntent(LauncherActivity.getStartIntent(this).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        APPOINTMENT_APPROVED_MESSAGE -> TaskStackBuilder.create(this).addNextIntent(LauncherActivity.getStartIntent(this).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        else -> TaskStackBuilder.create(this).addNextIntent(LauncherActivity.getStartIntent(this).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    companion object {
        private const val NEW_APPOINTMENT_MESSAGE = 1
        private const val APPOINTMENT_APPROVED_MESSAGE = 2
        private const val INTENT_BUILDER_REQUEST_CODE = 123

        private const val MY_MEDIC_NOTIFICATIONS_CHANNEL_ID = "my_medic_channel_01"
        private const val MY_MEDIC_CHANNEL_NAME = "MyMedicNotificationsChannel"
        private const val NOTIFICATION_TYPE_KEY = "type"
    }
}