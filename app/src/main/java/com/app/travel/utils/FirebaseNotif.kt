package com.app.travel.utils


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.travel.R
import com.app.travel.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var title: String? = ""
        var body: String? = ""
        var pendingIntent: PendingIntent? = null
        if (remoteMessage.notification != null) {
            title = remoteMessage.notification!!.title
            body = remoteMessage.notification!!.body
        }
        val notificationColor = resources.getColor(R.color.purple_500)
        val notificationIcon = notificationIcon
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = "ini"
        val notification_id = 123
        createNotificationChannel()
        val pattern = longArrayOf(500, 500, 500, 500, 500)

        //notif click foreground
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification =
            NotificationCompat.Builder(this, channelId).setContentTitle(title).setContentText(body)
                .setAutoCancel(true).setSmallIcon(notificationIcon).setColor(notificationColor)
                .setShowWhen(true).setVibrate(pattern).setSound(notificationSound)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX).setContentIntent(pendingIntent)
                .build()
        val manager = NotificationManagerCompat.from(applicationContext)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(notification_id, notification)
            return
        }

    }

    //khusus android versi Oreo
    private fun createNotificationChannel() {
        val channelId = "ini"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "ini"
            val description = "tes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description
            channel.vibrationPattern = longArrayOf(0L)
            channel.enableVibration(true)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val notificationIcon: Int
        private get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.mipmap.ic_launcher
        } else R.mipmap.ic_launcher

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("NEW_TOKEN", s)
    }
}