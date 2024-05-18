package com.karlom.bluetoothmessagingapp.feature.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetClientConnectedToMyServerNotifier
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartSavingReceivedMessages
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    getClientConnectedToMyServerNotifier: GetClientConnectedToMyServerNotifier,
    startSavingReceivedMessages: StartSavingReceivedMessages,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    navigator: Navigator,
) : ViewModel() {


    init {
        viewModelScope.launch(ioDispatcher) {
            startSavingReceivedMessages()
        }
        viewModelScope.launch {
            getClientConnectedToMyServerNotifier().collect {
                navigator.emitDestination(NavigationEvent.Destination(ChatRouter.creteChatRoute("stub")))
            }
        }
    }
}
