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
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.SeekBar
import com.pixelart.mediaplayerapp.utilities.Utilities


class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    private val TAG = "MediaPlayerService"
    private val CHANNEL_ID = "myNotificationChannel"

    private val iBinder = MyIBinder()
    val handler = Handler()

    lateinit var seekBar: SeekBar
    lateinit var musicPlayer: MediaPlayer
    lateinit var mediaDataRetriever: MediaMetadataRetriever
    private var currentSong: Int = 0
    private lateinit var musicPaths: ArrayList<String>

    lateinit var artist: TextView
    lateinit var title:TextView
    lateinit var currentDuration: TextView
    lateinit var totalDuration: TextView




    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        musicPlayer = MediaPlayer()
        musicPaths = ArrayList()
        mediaDataRetriever = MediaMetadataRetriever()
        seekBar = SeekBar(this)
        totalDuration = TextView(this)
        currentDuration = TextView(this)
        artist = TextView(this)
        title = TextView(this)

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
            initNotification()
            isPlaying()


        }



        return START_NOT_STICKY
    }

    fun playMusic()
    {
        musicPlayer = MediaPlayer()
        musicPlayer.setDataSource(musicPaths[currentSong])
        musicPlayer.prepareAsync()

        if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
            title.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

        if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
            artist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()

        musicPlayer.setOnPreparedListener(this)
        musicPlayer.setOnCompletionListener(this)


    }

    fun isPlaying(): Boolean{
        return musicPlayer.isPlaying
    }

    fun mediaControls(btnPlay : View, btnPause : View, btnPrev: View, btnNext: View, tvTitle: TextView, tvArtist: TextView)
    {
        artist = tvArtist
        title = tvTitle
        btnPause.setOnClickListener {
            if (isPlaying())
                musicPlayer.pause()
        }

        btnPlay.setOnClickListener{
            if (!isPlaying())
                musicPlayer.start()
        }

        btnNext.setOnClickListener {
            if (isPlaying())
            {
                currentSong++
                if ( currentSong >= musicPaths.size)
                {
                    musicPlayer.stop()
                    currentSong = 0
                }
                else {
                    musicPlayer.stop()
                    //currentSong++
                }
                playMusic()
                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
                    title.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
                    artist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
            }
        }

        btnPrev.setOnClickListener {
            val temp = musicPaths.size
            if (isPlaying())
            {
                if ( currentSong == 0)
                {
                    musicPlayer.stop()
                    currentSong = temp-1
                }
                else {
                    musicPlayer.stop()
                    currentSong--
                }
                playMusic()
                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
                    title.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
                    artist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
            }
        }
    }

    fun seekBar(seekBar: SeekBar, cDuration: TextView, tDuration: TextView)
    {
        this.seekBar = seekBar
        totalDuration = tDuration
        currentDuration = cDuration
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                {
                    musicPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateTimeTask())
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateTimeTask())
                musicPlayer.seekTo(seekBar!!.progress)
                updateSeekBar()
            }

        })
    }
    fun updateSeekBar()
    {
        handler.postDelayed(updateTimeTask(), 1000)
    }
    fun updateTimeTask() = object: Runnable
    {
        override fun run() {
            val utilities = Utilities()

            seekBar.progress = musicPlayer.currentPosition
            if (isPlaying())
            {
                val tDuration : Long = musicPlayer.duration.toLong()
                val cDuration : Long = musicPlayer.currentPosition.toLong()
                totalDuration.text = utilities.milliSecondsToTimer(tDuration)
                currentDuration.text = utilities.milliSecondsToTimer(cDuration)

                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
                    title.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

                if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
                    artist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()

                handler.postDelayed(this, 1000)
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
        seekBar.max = musicPlayer.duration
        musicPlayer.start()
        updateSeekBar()
        if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
            title.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

        if (mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
            artist.text = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
        if (!musicPlayer.isLooping) {
            currentSong++
            playMusic()
        }
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
