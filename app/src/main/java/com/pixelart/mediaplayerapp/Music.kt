package com.pixelart.mediaplayerapp

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(var title:String, var album:String, var artist:String, var duration:String, var coverImage:String, var path: String): Parcelable