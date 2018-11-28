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
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.support.v4.os.HandlerCompat.postDelayed




class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    private val TAG = "MediaPlayerService"
    private val CHANNEL_ID = "myNotificationChannel"

    private val iBinder = MyIBinder()

    lateinit var musicPlayer: MediaPlayer
    lateinit var mediaDataRetriever: MediaMetadataRetriever
    private var currentSong: Int = 0
    private lateinit var musicPaths: ArrayList<String>

    lateinit var artist: String
    lateinit var title:String



    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        musicPlayer = MediaPlayer()
        musicPaths = ArrayList()
        mediaDataRetriever = MediaMetadataRetriever()

    }

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        currentSong = intent!!.getIntExtra("musicPosition", 0)
        musicPaths = intent!!.getStringArrayListExtra("musicPaths")
        Log.d(TAG, musicPaths.toString())

        if (intent!!.action == "musicPlayer") {

            if (musicPlayer != null) {
                musicPlayer.stop()
                musicPlayer.reset()
                musicPlayer.release()
            }
            mediaDataRetriever.setDataSource(this, Uri.parse(musicPaths[currentSong]))
            playMusic()
            sendBroadcast()
            initNotification()
            isPlaying()


        }
       // TabActivity.newInstance().playPause(musicPlayer.isPlaying)

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

    fun isPlaying(): Boolean{
        return musicPlayer.isPlaying
    }

    fun mediaControls(btnPlay : View, btnPause : View, btnPrev: View, btnNext: View, tvTitle: TextView, tvArtist: TextView)
    {
        btnPause.setOnClickListener {
            if (isPlaying())
                musicPlayer.pause()
        }
        btnPlay.setOnClickListener{
            if (!isPlaying())
                musicPlayer.start()
        }


        if (isPlaying())
        {

        }



        /*val thread = object : Thread() {
            override fun run() {
                try {
                    while (true) {
                        if (isPlaying())
                        {
                            tvTitle.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()
                            tvArtist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
                        }
                        Thread.sleep(1000)
                        handler.post(this)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }*/
    }

    private fun sendBroadcast()
    {
        val isPlayingBroadcast = Intent()

        if (musicPlayer.isPlaying)
        {
            for (i in 0..100)
            {
                try {
                    Thread.sleep(1000)
                    isPlayingBroadcast.action = "isPlayingAction"
                    isPlayingBroadcast.putExtra("isPlaying", true)
                    sendBroadcast(isPlayingBroadcast)
                }catch (e:InterruptedException)
                {
                    e.printStackTrace()
                }
            }
        }
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


        val notificationIntent = Intent(this, TabActivity::class.java)
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

        if (isPlaying())
        {
            title = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()
            artist = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
        }

        val musicBroadcast = Intent()
        val handler = Handler()
        for (i : Int in 1..100)
        {


        }
        while (isPlaying())
        {
            try {
                Thread.sleep(1000)

                musicBroadcast.action = "musicAction"
                musicBroadcast.putExtra("title", title)
                musicBroadcast.putExtra("artist", artist)
                sendBroadcast(musicBroadcast)

                Log.d(TAG, "$artist, $title")
            }catch (e :InterruptedException)
            {
                e.printStackTrace()
            }
        }
        /*val r = Runnable {
            musicBroadcast.action = "musicAction"
            musicBroadcast.putExtra("title", title)
            musicBroadcast.putExtra("artist", artist)
            sendBroadcast(musicBroadcast)
        }
        handler.postDelayed(r, 1000)*/
        Toast.makeText(this, "$title $artist", Toast.LENGTH_SHORT).show()
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


    inner class MyIBinder : Binder(){
        fun getService(): MediaPlayerService
        {
            return this@MediaPlayerService
        }
    }


}
