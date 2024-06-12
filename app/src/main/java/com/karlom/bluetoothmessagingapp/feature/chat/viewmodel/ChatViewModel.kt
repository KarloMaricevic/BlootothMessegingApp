package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.extensions.combine
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetConnectedDevicesNotifier
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.IsConnectedToDevice
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.ConnectToServer
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.GetMessages
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendImage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartChatServerAndWaitForConnection
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.*
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.*
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@HiltViewModel(assistedFactory = ChatViewModel.ChatViewModelFactory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted private val contactAddress: String,
    private val getMessages: GetMessages,
    private val sendMessage: SendMessage,
    private val isConnectedToDevice: IsConnectedToDevice,
    private val sendImage: SendImage,
    private val getConnectionStateNotifier: GetConnectedDevicesNotifier,
    private val startChatServerAndWaitForConnection: StartChatServerAndWaitForConnection,
    private val connectToServer: ConnectToServer,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<ChatScreenEvent>() {

    private companion object {
        const val NUMBER_OF_RETRIES_WHEN_CONNECTING = 3
        const val RETRY_DELAY_MILLIS = 3000L
    }

    private val showConnectToDeviceButton = MutableStateFlow(!isConnectedToDevice(contactAddress))
    private val isTryingToConnect = MutableStateFlow(false)
    private val textToSend = MutableStateFlow("")
    private val inputMode = MutableStateFlow(TEXT)
    private val isRecordingVoice = MutableStateFlow(false)
    private val messages = MutableStateFlow(getMessages(contactAddress))

    val state = combine(
        showConnectToDeviceButton,
        isTryingToConnect,
        textToSend,
        inputMode,
        isRecordingVoice,
        messages,
        ::ChatScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = ChatScreenState(
            showConnectToDeviceButton = !isConnectedToDevice(contactAddress)
        ),
    )

    init {
        viewModelScope.launch {
            getConnectionStateNotifier().collect { connectedDevices ->
                showConnectToDeviceButton.update { connectedDevices.firstOrNull { it.address == contactAddress } == null }
            }
        }
    }

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is OnTextChanged -> textToSend.update { event.text }
            is OnSendClicked -> viewModelScope.launch {
                sendMessage(message = textToSend.value, address = contactAddress)
            }

            is OnConnectClicked -> startServerAndPeriodicallyTryToConnectToAddress()

            is OnSendImageClicked -> viewModelScope.launch(ioDispatcher) {
                sendImage(imageUri = event.uri, address = contactAddress)
            }

            is OnStartRecordingVoiceClicked -> {
                inputMode.update { VOICE }
                isRecordingVoice.update { true }
            }

            is OnStopRecordingVoiceClicked -> {
                isRecordingVoice.update { false }
            }

            is OnDeleteVoiceRecordingClicked -> {
                isRecordingVoice.update { false }
                inputMode.update { TEXT }
            }
        }
    }

    private fun startServerAndPeriodicallyTryToConnectToAddress() {
        isTryingToConnect.update { true }
        viewModelScope.launch(ioDispatcher) {
            val waitForClientJob = async(ioDispatcher) { startChatServerAndWaitForConnection() }
            val tryToConnectJob = async(ioDispatcher) {
                var connectToServer: Either<Failure.ErrorMessage, BluetoothDevice>? = null
                repeat(NUMBER_OF_RETRIES_WHEN_CONNECTING) { timesRun ->
                    val connected = connectToServer(contactAddress)
                    connected.onLeft {
                        if (timesRun < NUMBER_OF_RETRIES_WHEN_CONNECTING - 1) {
                            delay(RETRY_DELAY_MILLIS)
                        } else {
                            connectToServer = connected
                        }
                    }
                    connected.onRight {
                        connectToServer = connected
                        return@repeat
                    }
                }
                connectToServer!!
            }
            select {
                waitForClientJob.onAwait { result ->
                    tryToConnectJob.cancelAndJoin()
                    result
                }
                tryToConnectJob.onAwait { result ->
                    waitForClientJob.cancelAndJoin()
                    result
                }
            }
            isTryingToConnect.update { false }
        }
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(contactAddress: String): ChatViewModel
    }
}
