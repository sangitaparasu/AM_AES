package com.example.am_aes.ViewModel

import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.am_aes.Repository.MessageRepository
import com.example.am_aes.Utils.AESUtils
import com.example.am_aes.Utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SenderViewModel @Inject constructor(@Named("room") private val repository: MessageRepository) :
    ViewModel() {
    private val _receivedMessages: MutableLiveData<Resource<List<Pair<String, List<Pair<String, Long>>>>>> = MutableLiveData()

    val receivedMessages: LiveData<Resource<List<Pair<String, List<Pair<String, Long>>>>>>
        get() = _receivedMessages

    fun sendMessage(number: String,message: String, currentTimeMillis: Long) {
        viewModelScope.launch {
            try {
                val msg=AESUtils.encrypt(message)
                val phno=AESUtils.encrypt(number)
                repository.saveMessage(phno,msg,currentTimeMillis)
                sendSMS(number, msg)
                getMessages()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun sendSMS(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    fun getMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            _receivedMessages.postValue(Resource.loading())

            try {
                repository.getMessages().collect { messages ->
                    _receivedMessages.postValue(Resource.success(messages))
                }
            } catch (e: Exception) {
                _receivedMessages.postValue(Resource.error(e.toString(), null))
            }
        }
    }
}
