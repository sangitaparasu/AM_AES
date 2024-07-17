package com.example.am_aes.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.am_aes.Repository.MessageRepository
import com.example.am_aes.Utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ReceiverViewModel @Inject constructor(@Named("room") private val messageRepository: MessageRepository) : ViewModel() {

    private val _receivedMessages: MutableLiveData<Resource<List<Pair<String, List<Pair<String, Long>>>>>> = MutableLiveData()

    val receivedMessages: LiveData<Resource<List<Pair<String, List<Pair<String, Long>>>>>>
        get() = _receivedMessages

    fun getMessages() {
        Log.d("rshrepreview","called")
        viewModelScope.launch(Dispatchers.IO) {
            _receivedMessages.postValue(Resource.loading())

            try {
                Log.d("rshrepreview","called1")
                messageRepository.getMessages().collect { messages ->
                    _receivedMessages.postValue(Resource.success(messages))
                }
            } catch (e: Exception) {
                Log.d("rshrepreview","called2")
                _receivedMessages.postValue(Resource.error(e.toString(), null))
            }
        }
    }
}

