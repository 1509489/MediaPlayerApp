package com.pixelart.mediaplayerapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.FileNotFoundException
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var musicList: List<Music>

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicList = ArrayList()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
        else
            initMusic()

        layoutManager = LinearLayoutManager(this)
        rvMusicList.layoutManager = layoutManager

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            initMusic()
        }
        else
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()

        adapter = RecyclerViewAdapter(this,musicList)
        rvMusicList.adapter = adapter
        adapter.notifyDataSetChanged()

        for(i in musicList.indices)
        {
            var music = Music(musicList[i].title, musicList[i].album, musicList[i].artist, musicList[i].duration, musicList[i].coverImage, musicList[i].path)
            //Log.d(TAG, music.toString())
        }
    }

    fun initMusic()
    {
        var cursor : Cursor? = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
            null, null, MediaStore.Audio.Media.TITLE + " ASC")

        val albumArtCursor: Cursor? = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null)


       /* var albumArtCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf<String>(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART),
            MediaStore.Audio.Albums._ID + "=?",
            albulmId,
            null)*/


        if(cursor!!.moveToFirst())
        {
            do {
                var title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                var album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                var duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                var artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                var albumid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))

                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumid)
                //Log.d(TAG, albumArtUri.toString())

              /*  var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, albumArtUri)
                    bitmap = Bitmap.createScaledBitmap(bitmap!!, 30, 30, true)

                } catch (exception: FileNotFoundException) {
                    exception.printStackTrace()
                    bitmap = BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                } catch (e: IOException) {

                    e.printStackTrace()
                }
                var coverImage = bitmap
                Log.d(TAG, coverImage.toString())*/
                (musicList as ArrayList<Music>).add(Music(title, album, artist, duration, "ic_launcher_background", path))
            } while (cursor.moveToNext())
        }
    }
}
