package com.pixelart.mediaplayerapp.adapters

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.pixelart.mediaplayerapp.Music
import com.pixelart.mediaplayerapp.R
import java.util.concurrent.TimeUnit


class RecyclerViewAdapter(val context: Context, val musicList: List<Music>, val listener: OnItemClickListener): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {



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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]

        holder.tvTitle.text = music.title
        holder.tvArtist.text = music.artist
        holder.tvAlbum.text = music.album
        holder.tvDuration.text = toMinandSec(music.duration.toLong())

        val image = context.resources.getIdentifier(music.coverImage, "drawable", context.packageName)
        holder.ivCoverImage.setImageResource(image)

        holder.itemView.setOnClickListener{

            listener.onItemClickedListener(position)
        }
    }

    fun toMinandSec(millisecond: Long): String
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

    interface OnItemClickListener{
        fun onItemClickedListener(position: Int){

        }
    }
}
