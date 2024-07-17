package com.example.am_aes.ui.home


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.am_aes.Adapter.MessageAdapter
import com.example.am_aes.Model.MessageItem
import com.example.am_aes.R
import com.example.am_aes.Utils.Resource
import com.example.am_aes.ViewModel.SenderViewModel
import com.example.am_aes.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SenderViewModel by viewModels()

    private val messageList = mutableListOf<MessageItem>()
    private lateinit var messageAdapter: MessageAdapter

    companion object {
        const val REQUEST_SMS_PERMISSION = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageAdapter = MessageAdapter(messageList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = messageAdapter


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
                                MessageItem(phoneNumber, message, timestamp.toString(), messageList)
                            }
                        }
                        messageList.clear()
                        messageList.addAll(messageItems)
                        messageAdapter.notifyDataSetChanged()
                    }

                }

                Resource.Status.ERROR -> {

                    Log.e(TAG, "Error Receiving messages: ${resource.message}")
                }

            }
        }

        binding.fabAddMessage.setOnClickListener {

            showAddMessageDialog()

        }


        return root
    }

    private fun sendMessage(number: String, message: String) {

        if (number.isNotEmpty()&&message.isNotEmpty()) {

                val currentTimeMillis = System.currentTimeMillis()
                viewModel.sendMessage(number, message, currentTimeMillis)


        }
        else {

            if (number.isEmpty()) {
                showToast("Phone number cannot be empty")
            }
            if (message.isEmpty()) {
                showToast("Message cannot be empty")
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddMessageDialog() {

        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.newchatdesign, null)
        val phonenumber = dialogView.findViewById<EditText>(R.id.editTextmobilenumber)
        val message = dialogView.findViewById<EditText>(R.id.editTextMessage)
        val buttonSend = dialogView.findViewById<Button>(R.id.buttonSend)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val dialog = builder.create()

        buttonSend.setOnClickListener {

            sendMessage(phonenumber.text.toString(), message.text.toString())
            dialog.dismiss()


        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}