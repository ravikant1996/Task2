package com.example.task2

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.task2.databinding.ActivityMainBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.show.setOnClickListener {
            addNotification()
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun addNotification() {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = "Lorem Picsum"
        val body = "The Lorem Ipsum for photos."

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            var bigPicture: Bitmap? = null
            val service = Executors.newSingleThreadExecutor() as ExecutorService
            service.execute {
                try {
                    val url = URL(src)
                    val connection = url
                        .openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    bigPicture = BitmapFactory.decodeStream(input)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                runOnUiThread {
                    builder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(bigPicture)
                            .setSummaryText(body)
                    )
                    with(NotificationManagerCompat.from(this)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(0, builder.build())
                    }
                }
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


    }

    companion object {
        const val CHANNEL_ID: String = "MyChannel"
        const val CHANNEL_NAME: String = "MyChannelName"
        const val CHANNEL_DESCRIPTION: String = "MyChannelDescription"
        const val src = "https://picsum.photos/300/200"

    }
}