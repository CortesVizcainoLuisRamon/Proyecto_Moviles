package com.example.activaescom

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.graphics.BitmapFactory

class RecordatorioWorker(

    context: Context,

    workerParams: WorkerParameters

) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val channelId =
            "entrenaipn_channel"

        val notificationManager =

            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(

            channelId,

            "EntrenaIPN Recordatorios",

            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager
            .createNotificationChannel(channel)

        val largeIcon =

            BitmapFactory.decodeResource(

                applicationContext.resources,

                R.drawable.entrena_ipn
            )

        val notification =

            NotificationCompat.Builder(
                applicationContext,
                channelId
            )

                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )

                .setLargeIcon(
                    largeIcon
                )

                .setContentTitle(
                    "🏃 Hora de entrenar"
                )

                .setContentText(
                    "Tu sesión fitness te espera 🔥"
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

        return Result.success()
    }
}