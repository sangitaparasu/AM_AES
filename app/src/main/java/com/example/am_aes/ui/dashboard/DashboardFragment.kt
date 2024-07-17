package com.example.am_aes.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.am_aes.Adapter.MessageAdapter
import com.example.am_aes.Model.MessageItem
import com.example.am_aes.Utils.Resource
import com.example.am_aes.ViewModel.ReceiverViewModel
import com.example.am_aes.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentDashboardBinding? = null


    private val receivedMessageList = mutableListOf<MessageItem>()
    private lateinit var messageAdapter: MessageAdapter

    val viewModel: ReceiverViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageAdapter = MessageAdapter(receivedMessageList)
        binding.recvrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recvrecyclerView.adapter = messageAdapter
        viewModel.getMessages()
        viewModel.receivedMessages.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {

                    Log.d(TAG, "Loading...")

                }

                Resource.Status.SUCCESS -> {

                    Log.d(TAG, "Success: ${resource.data}")
                    resource.data?.let { messages ->
                        val messageItems = messages.mapNotNull { (phoneNumber, messageList) ->
                            val latestMessagePair = messageList.maxByOrNull { it.second }
                            latestMessagePair?.let { (message, timestamp) ->
                                MessageItem(phoneNumber, message, timestamp.toString(),messageList)
                            }
                        }
                        receivedMessageList.clear()
                        receivedMessageList.addAll(messageItems)
                        messageAdapter.notifyDataSetChanged()
                    }

                }

                Resource.Status.ERROR -> {

                    Log.e(TAG, "Error Receiving messages: ${resource.message}")
                }

            }
        }

        return root

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}