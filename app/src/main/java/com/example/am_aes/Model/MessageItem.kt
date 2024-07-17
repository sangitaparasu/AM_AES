package com.example.am_aes.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageItem(
    val number: String,
    val message: String,
    val time: String,
    val mesagelist: List<Pair<String, Long>>
): Parcelable
