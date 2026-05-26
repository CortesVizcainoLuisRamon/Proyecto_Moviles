package com.example.activaescom

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Toast.makeText(

            context,

            "Receiver ejecutado",

            Toast.LENGTH_LONG

        ).show()

        val channelId =
            "entrenaipn_channel"

        val notificationManager =

            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        // Crear canal Android 8+
        val channel = NotificationChannel(

            channelId,

            "EntrenaIPN Recordatorios",

            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager
            .createNotificationChannel(channel)


        val largeIcon =

            BitmapFactory.decodeResource(

                context.resources,

                R.drawable.entrena_ipn
            )

        val notification =

            NotificationCompat.Builder(
                context,
                channelId
            )

                // Icono temporal seguro
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )

                .setContentTitle(
                    "🏃 Hora de entrenar"
                )

                .setContentText(
                    "Tu sesión fitness te espera 🔥"
                )

                .setLargeIcon(
                    largeIcon
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)

                .build()

        notificationManager.notify(

            1,

            notification
        )
    }
}