package com.example.am_aes.ui.notifications


import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.am_aes.Adapter.MessageDetailAdapter
import com.example.am_aes.Model.MessageDetail
import com.example.am_aes.Model.MessageItem
import com.example.am_aes.ViewModel.SenderViewModel
import com.example.am_aes.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var messageDetailAdapter: MessageDetailAdapter

    private val viewModel: SenderViewModel by viewModels()
    private val binding get() = _binding!!
    private val messageList = mutableListOf<MessageDetail>()
    var phoneNumber = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageDetailAdapter = MessageDetailAdapter(messageList)
        binding.recyclerdeatils.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerdeatils.adapter = messageDetailAdapter

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayTimestamp = cal.timeInMillis

        messageList.add(
            MessageDetail(
                "random check",
                yesterdayTimestamp
            )
        )
        arguments?.let {
            phoneNumber = it.getString("phoneNumber").toString()

            val activity = requireActivity() as AppCompatActivity
            activity.supportActionBar?.title = phoneNumber

            val mlist = it.getParcelableArray("messageList")?.map { it as MessageItem }
            mlist?.forEach { messageItem ->
                messageItem.mesagelist.forEach { (message, timestamp) ->


                    val messageDetail = MessageDetail(
                        message,
                        timestamp
                    )

                    messageList.add(messageDetail)


                }

                val position = messageList.size - 1
                messageDetailAdapter.notifyItemInserted(position)
                binding.recyclerdeatils.scrollToPosition(position)
                Log.d("Details", phoneNumber + "  :  " + messageList.toString())
            }
        }
        binding.detailSend.setOnClickListener {

            sendMessage()

        }


        return root
    }

    private fun sendMessage() {
        val message = binding.detailMsgDescrip.text.toString()
        if (message.isNotEmpty()) {

            val currentTimeMillis = System.currentTimeMillis()
            viewModel.sendMessage(phoneNumber, message, currentTimeMillis)
            messageList.add(
                MessageDetail(
                    message,
                    currentTimeMillis
                )
            )
            val position = messageList.size - 1
            messageDetailAdapter.notifyItemInserted(position)
            binding.recyclerdeatils.scrollToPosition(position)
            binding.detailMsgDescrip.text?.clear()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}