package com.pixelart.mediaplayerapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.support.v4.app.NotificationCompat
import android.app.PendingIntent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build





class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    private val TAG = "MediaPlayerService"
    private val CHANNEL_ID = "myNotificationChannel"

    lateinit var musicPlayer: MediaPlayer
    private var currentSong: Int = 0
    private lateinit var musicPaths: ArrayList<String>


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        musicPlayer = MediaPlayer()
        musicPaths = ArrayList()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        currentSong = intent!!.getIntExtra("musicPosition", 0)
        musicPaths = intent!!.getStringArrayListExtra("musicPaths")
        Log.d(TAG, musicPaths.toString())

        if (musicPlayer != null)
        {
            musicPlayer.stop()
            musicPlayer.reset()
            musicPlayer.release()
        }
        playMusic()
        initNotification()


        return START_NOT_STICKY
    }

    fun playMusic()
    {
        musicPlayer = MediaPlayer()
        musicPlayer.setDataSource(musicPaths[currentSong])
        musicPlayer.prepareAsync()
        musicPlayer.setOnPreparedListener(this)
        musicPlayer.setOnCompletionListener(this)
        currentSong++
    }

    private fun initNotification() {

        var contentTitle: CharSequence = "Now Playing"
        var contentText: CharSequence = "Playing music"

        val channel_name = "MusicPlayerChannel"
        val channel_description = "Music Player foreground service channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance)
            channel.description = channel_description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("stopMusic", "stopMusic")
        notificationIntent.action = "stopMusic"

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle(contentTitle).setContentText(contentText)
            .setContentIntent(pendingIntent).addAction(0, "Stop Music", pendingIntent)
            .setAutoCancel(true).build()

        startForeground(1, notificationBuilder)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        musicPlayer.stop()
        musicPlayer.reset()
        musicPlayer.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        musicPlayer.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
        if (!musicPlayer.isLooping)
            playMusic()
        else
            return

    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {


            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->

                Toast.makeText(
                    this,

                    "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra",

                    Toast.LENGTH_SHORT
                ).show()

            MediaPlayer.MEDIA_ERROR_SERVER_DIED ->

                Toast.makeText(
                    this, "MEDIA ERROR SERVER DIED $extra",

                    Toast.LENGTH_SHORT
                ).show()

            MediaPlayer.MEDIA_ERROR_UNKNOWN ->

                Toast.makeText(
                    this, "MEDIA ERROR UNKNOWN $extra",

                    Toast.LENGTH_SHORT
                ).show()
        }

        return false
    }
}
