package com.example.am_aes.Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.am_aes.Model.MessageItem
import com.example.am_aes.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


class MessageAdapter(private val messageList: List<MessageItem>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chatdesign, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = messageList[position]
        val firstChar = currentItem.message.firstOrNull()?.let { char ->
            if (char.isLetter()) {
                char.uppercaseChar().toString()
            } else {
                char.toString()
            }
        } ?: ""
        holder.userProfileImage.text =firstChar
        holder.displayNameText.text = currentItem.number
        holder.messageText.text = currentItem.message
        holder.timeText.text = formatTime(currentItem.time.toLong())
        val randomColor = getRandomColor()
        Log.d("RandomColor", "Color: $randomColor")

        holder.imageCardView.setCardBackgroundColor(randomColor)
        Log.d("time",currentItem.time)
        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "phoneNumber" to currentItem.number,
                "messageList" to arrayOf(currentItem)
            )

            it.findNavController().navigate(R.id.navigation_notifications, bundle)
        }

    }
    fun formatTime(milliseconds: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return sdf.format(calendar.time)
    }
    override fun getItemCount() = messageList.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfileImage: TextView = itemView.findViewById(R.id.userProfileImage)
        val displayNameText: TextView = itemView.findViewById(R.id.displayNameText)
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val imageCardView: CardView = itemView.findViewById(R.id.imageCardView)
    }
    private fun getRandomColor(): Int {
        val low = 64
        val high = 192
        val r = Random.nextInt(low, high)
        val g = Random.nextInt(low, high)
        val b = Random.nextInt(low, high)
        return Color.rgb(r, g, b)
    }
}