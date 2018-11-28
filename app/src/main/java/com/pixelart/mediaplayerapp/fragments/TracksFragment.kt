package com.pixelart.mediaplayerapp.fragments


import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pixelart.mediaplayerapp.MediaPlayerService
import com.pixelart.mediaplayerapp.Music

import com.pixelart.mediaplayerapp.R
import com.pixelart.mediaplayerapp.adapters.RecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_tracks.*


class TracksFragment : Fragment() {
    private val TAG = "TracksFragment"
    private lateinit var mContext: Context

    private lateinit var musicList: List<Music>
    lateinit var allMusicPath:List<String>

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerViewAdapter

    companion object {
        fun newInstance() = TracksFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        musicList = ArrayList()
        allMusicPath = ArrayList()
        mContext = context!!.applicationContext

        layoutManager = LinearLayoutManager(context)
        rvTracks.layoutManager = layoutManager
        initMusic()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tracks, container, false)



        return view
    }

    override fun onResume() {
        super.onResume()

        adapter = RecyclerViewAdapter(
            mContext,
            musicList,
            object : RecyclerViewAdapter.OnItemClickListener {
                override fun onItemClickedListener(position: Int) {
                    super.onItemClickedListener(position)

                    for (i in 0 until musicList.size) {
                        (allMusicPath as ArrayList<String>).add(musicList[i].path)

                    }
                    Log.d(TAG, "all music path $allMusicPath")
                    var intent = Intent(context, MediaPlayerService::class.java)
                    intent.action = "musicPlayer"
                    intent.putStringArrayListExtra("musicPaths", allMusicPath as ArrayList<String>)
                    intent.putExtra("musicPosition", position)
                    mContext.startService(intent)
                    mContext.sendBroadcast(intent)
                }
            })
        rvTracks.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun initMusic()
    {
        val selection = "${MediaStore.Audio.Media.IS_MUSIC}!=0"
        var cursor : Cursor? = context!!.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
            selection, null, MediaStore.Audio.Media.TITLE + " ASC")

        if(cursor!!.moveToFirst())
        {
            do {
                var title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                var album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                var duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                var artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))


                (musicList as ArrayList<Music>).add(Music(title, album, artist, duration, "ic_launcher_background", path))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

}
