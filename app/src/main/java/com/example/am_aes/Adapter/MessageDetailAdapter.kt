package com.example.am_aes.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.am_aes.Model.MessageDetail
import com.example.am_aes.R
import java.text.SimpleDateFormat
import java.util.*

class MessageDetailAdapter(private val messageList: List<MessageDetail>) : RecyclerView.Adapter<MessageDetailAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.msgdesign, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = messageList[position]
        if (position == 0 || !isSameDate(currentItem.timestamp, messageList[position - 1].timestamp)) {
            holder.commonDateText.visibility = View.VISIBLE
            holder.commonDateText.text = formatDate(currentItem.timestamp)
        } else {
            holder.commonDateText.visibility = View.GONE
        }

        holder.messageText.text = currentItem.message
        holder.timestampText.text = formatTime(currentItem.timestamp)
    }
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    override fun getItemCount() = messageList.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.detailMessage)
        val timestampText: TextView = itemView.findViewById(R.id.detailTime)
        val commonDateText: TextView = itemView.findViewById(R.id.commonDate)
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    private fun isSameDate(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = timestamp1
        cal2.timeInMillis = timestamp2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}
