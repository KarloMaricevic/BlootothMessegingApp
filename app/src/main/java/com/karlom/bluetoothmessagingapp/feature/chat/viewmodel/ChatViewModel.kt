package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
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
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendAudio
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendImage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartChatServerAndWaitForConnection
import com.karlom.bluetoothmessagingapp.domain.audio.PauseAudio
import com.karlom.bluetoothmessagingapp.domain.audio.PlayAudioFile
import com.karlom.bluetoothmessagingapp.domain.audio.PlayInitializedAudio
import com.karlom.bluetoothmessagingapp.domain.audio.ReleaseAudioPlayer
import com.karlom.bluetoothmessagingapp.domain.audio.StartRecordingVoice
import com.karlom.bluetoothmessagingapp.domain.audio.StopPlayingAudio
import com.karlom.bluetoothmessagingapp.domain.audio.StopRecordingVoice
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.ChatItemMapper
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnDeleteVoiceRecordingClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnPausePlayingAudioMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnPlayAudioMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStopRecordingVoiceClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.UUID

@HiltViewModel(assistedFactory = ChatViewModel.ChatViewModelFactory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted private val contactAddress: String,
    private val getMessages: GetMessages,
    private val sendMessage: SendMessage,
    private val isConnectedToDevice: IsConnectedToDevice,
    private val sendImage: SendImage,
    private val getConnectionStateNotifier: GetConnectedDevicesNotifier,
    private val startChatServerAndWaitForConnection: StartChatServerAndWaitForConnection,
    private val startRecordingVoice: StartRecordingVoice,
    private val stopRecordingVoice: StopRecordingVoice,
    private val sendAudio: SendAudio,
    private val connectToServer: ConnectToServer,
    private val playAudio: PlayAudioFile,
    private val playInitializedAudio: PlayInitializedAudio,
    private val pauseAudio: PauseAudio,
    private val stopPlayingAudio: StopPlayingAudio,
    private val releaseAudioPlayer: ReleaseAudioPlayer,
    private val chatItemMapper: ChatItemMapper,
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
    private val audioMessagePlaying = MutableStateFlow<ChatItem.Audio?>(null)
    private val isRecordingVoice = MutableStateFlow(false)
    private val messages = MutableStateFlow(getMessages(contactAddress).map { page ->
        page.map { message -> chatItemMapper.map(message) }
    }.cachedIn(viewModelScope).combine(audioMessagePlaying) { page, audioPlaying ->
        page.map { message ->
            if (audioPlaying != null && message is Audio && message.id == audioPlaying.id) {
                audioPlaying
            } else {
                message
            }
        }
    })

    private var voiceFileUri: String? = null

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
                when (inputMode.value) {
                    TEXT -> sendMessage(message = textToSend.value, address = contactAddress)
                    VOICE -> voiceFileUri?.let { notNullFileUri ->
                        sendAudio(
                            imageUri = notNullFileUri,
                            address = contactAddress,
                        )
                    }
                }
            }

            is OnConnectClicked -> startServerAndPeriodicallyTryToConnectToAddress()

            is OnSendImageClicked -> viewModelScope.launch(ioDispatcher) {
                sendImage(imageUri = event.uri, address = contactAddress)
            }

            is OnStartRecordingVoiceClicked -> {
                val recording = startRecordingVoice(UUID.randomUUID().toString())
                recording.onRight { fileUri ->
                    voiceFileUri = fileUri
                    inputMode.update { VOICE }
                    isRecordingVoice.update { true }
                }
            }

            is OnStopRecordingVoiceClicked -> {
                stopRecordingVoice()
                isRecordingVoice.update { false }
            }

            is OnDeleteVoiceRecordingClicked -> {
                stopRecordingVoice()
                voiceFileUri = null
                isRecordingVoice.update { false }
                inputMode.update { TEXT }
            }

            is OnPausePlayingAudioMessage -> {
                pauseAudio()
                audioMessagePlaying.update { current -> current?.copy(isPlaying = false) }
            }

            is OnPlayAudioMessage -> {
                val currentPlayingMessage = audioMessagePlaying.value
                if (currentPlayingMessage != null && currentPlayingMessage.id == event.message.id) {
                    playInitializedAudio().onRight {
                        audioMessagePlaying.update { event.message.copy(isPlaying = true) }
                    }
                } else {
                    stopPlayingAudio()
                    viewModelScope.launch {
                        playAudio(event.message.audioUri).onRight {
                            audioMessagePlaying.update { event.message.copy(isPlaying = true) }
                        }
                    }
                }
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

    override fun onCleared() {
        releaseAudioPlayer()
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(contactAddress: String): ChatViewModel
    }
}
