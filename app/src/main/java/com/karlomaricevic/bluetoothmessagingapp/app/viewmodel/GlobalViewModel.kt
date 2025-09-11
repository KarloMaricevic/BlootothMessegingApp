package com.karlomaricevic.bluetoothmessagingapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.CloseConnection
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.StartSavingReceivedMessages
import kotlinx.coroutines.launch

class GlobalViewModel(
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
