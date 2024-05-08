package com.karlom.bluetoothmessagingapp.feature.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetClientConnectedToMyServerNotifier
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    getClientConnectedToMyServerNotifier: GetClientConnectedToMyServerNotifier,
    navigator: Navigator,
) : ViewModel() {


    init {
        viewModelScope.launch {
            getClientConnectedToMyServerNotifier().collect {
                navigator.emitDestination(NavigationEvent.Destination(ChatRouter.creteChatRoute("stub")))
            }
        }
    }
}
