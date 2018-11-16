package com.pixelart.mediaplayerapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {


    private val TAG = "MediaPlayerService"

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
