package com.pixelart.mediaplayerapp

import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import java.sql.Time
import java.util.concurrent.TimeUnit


class RecyclerViewAdapter(val context: Context, val musicList: List<Music>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //private var context: Context? = null

    companion object {
        private val TAG = "RecyclerViewAdapter"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")


        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position];

        holder.tvTitle.text = music.title
        holder.tvArtist.text = music.artist
        holder.tvAlbum.text = music.album
        holder.tvDuration.text = toMandS(music.duration.toLong())

        val image = context.resources.getIdentifier(music.coverImage, "drawable", context.packageName)
        holder.ivCoverImage.setImageResource(image)

        holder.itemView.setOnClickListener{
            Toast.makeText(context, "item clicked at position: ${music.title}\n $position", Toast.LENGTH_SHORT).show()
        }
    }

    fun toMandS(millisecond: Long): String
    {
        var duration = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisecond),
            TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millisecond)
            ))
        return duration
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvTitle: TextView
        var tvArtist: TextView
        var tvAlbum: TextView
        var tvDuration: TextView
        var ivCoverImage: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvArtist = itemView.findViewById(R.id.tvArtist)
            tvAlbum = itemView.findViewById(R.id.tvAlbum)
            tvDuration = itemView.findViewById(R.id.tvDuration)
            ivCoverImage = itemView.findViewById(R.id.ivCoverImage)
        }
    }
}
