package com.karlom.bluetoothmessagingapp.feature.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.domain.connection.usecase.CloseAllConnections
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartSavingReceivedMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val closeAllConnections: CloseAllConnections,
    startSavingReceivedMessages: StartSavingReceivedMessages,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : ViewModel() {


    init {
        viewModelScope.launch(ioDispatcher) { startSavingReceivedMessages() }
    }

    override fun onCleared() {
        closeAllConnections()
        super.onCleared()
    }
}
