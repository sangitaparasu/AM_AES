package com.example.am_aes.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageDetail(
    val message: String,
    val timestamp: Long
) : Parcelable