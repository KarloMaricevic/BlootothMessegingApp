package com.karlomaricevic.bluetoothmessagingapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlomaricevic.domain.connection.usecase.CloseConnection
import com.karlomaricevic.domain.messaging.usecase.StartSavingReceivedMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val closeConnection: CloseConnection,
    startSavingReceivedMessages: StartSavingReceivedMessages,
) : ViewModel() {


    init {
        viewModelScope.launch { startSavingReceivedMessages() }
    }

    override fun onCleared() {
        closeConnection()
        super.onCleared()
    }
}
