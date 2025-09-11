package com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.flatMap
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcher
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.ChatMessageMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.DateIndicatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.SeparatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect.*
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.BaseViewModel
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.DeleteAudio
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.GetAudioPlayer
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.GetVoiceRecorder
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ConnectToKnownContact
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ObserveConnectionState
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.IsConnectedTo
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.*
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.GetMessages
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendAudio
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendImage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendText
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnBackClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnDeleteVoiceRecordingClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnPausePlayingAudioMessage
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStopRecordingVoiceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatViewModelParams
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.TIMEOUT_DELAY
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.extensions.combine
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.ChatViewModelFactory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted private val params: ChatViewModelParams,
    private val getMessages: GetMessages,
    private val sendText: SendText,
    private val isConnectedTo: IsConnectedTo,
    private val sendImage: SendImage,
    private val getAudioPlayer: GetAudioPlayer,
    private val getConnectionStateNotifier: ObserveConnectionState,
    private val getVoiceRecorder: GetVoiceRecorder,
    private val deleteAudio: DeleteAudio,
    private val sendAudio: SendAudio,
    private val connectToKnownContact: ConnectToKnownContact,
    private val chatMessageMapper: ChatMessageMapper,
    private val dateIndicatorMapper: DateIndicatorMapper,
    private val separatorMapper: SeparatorMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val navigator: ChatNavigator,
) : BaseViewModel<ChatScreenEvent>() {

    private companion object {
        const val NUMBER_OF_RETRIES_WHEN_CONNECTING = 3
        const val RETRY_DELAY_MILLIS = 3000L
    }

    private val audioPlayer = getAudioPlayer()
    private val voiceRecorder = getVoiceRecorder()

    private val showConnectToDeviceButton = MutableStateFlow(!isConnectedTo(params.address))
    private val isTryingToConnect = MutableStateFlow(false)
    private val textToSend = MutableStateFlow("")
    private val inputMode = MutableStateFlow(TEXT)
    private val audioMessagePlaying = MutableStateFlow<Audio?>(null)
    private val isRecordingVoice = MutableStateFlow(false)
    private val messages: StateFlow<List<ChatItem>> = getMessages(params.address)
        .map { messages ->
            val estimatedSize = messages.size + messages.size / 10
            val result = ArrayList<ChatItem>(estimatedSize)
            for (i in messages.indices) {
                val before = if (i > 0) messages[i - 1] else null
                val current = messages[i]
                if (before == null || !areSameDate(before.timestamp, current.timestamp)) {
                    result.add(dateIndicatorMapper.map(current))
                }
                result.add(chatMessageMapper.map(current))
                val after = if (i < messages.size - 1) messages[i + 1] else null
                separatorMapper.map(current, after)?.let { result.add(it) }
            }
            result
        }
        .combine(audioMessagePlaying) { chatItems, audioPlaying ->
            chatItems.map { message ->
                if (audioPlaying != null && message is Audio && message.id == audioPlaying.id) {
                    audioPlaying
                } else {
                    message
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var voiceRecordingFilePath: String? = null

    val state = combine(
        flowOf(false),
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
            showConnectToDeviceButton = !isConnectedTo(params.address)
        ),
    )

    private val _viewEffect = Channel<ChatScreenEffect>(Channel.BUFFERED)
    val viewEffect = _viewEffect.receiveAsFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            getConnectionStateNotifier().collect { connectedDevices ->
                showConnectToDeviceButton.update { connectedDevices?.address == params.address }
            }
        }
    }

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is OnBackClicked -> viewModelScope.launch { navigator.navigateBack() }
            is OnTextChanged -> textToSend.update { event.text }
            is OnSendClicked -> viewModelScope.launch(ioDispatcher) {
                when (inputMode.value) {
                    TEXT -> sendText(message = textToSend.value, address = params.address)

                    VOICE -> voiceRecordingFilePath?.let { voiceRecordingFilePath ->
                        sendAudio(imagePath = voiceRecordingFilePath, address = params.address)
                    }
                }?.collect { state ->
                    if (state == SENDING) {
                        _viewEffect.trySend(ScrollToBottom)
                    }
                }
                textToSend.update { "" }
                inputMode.update { TEXT }
                voiceRecordingFilePath = null
            }

            is OnConnectClicked -> startServerAndPeriodicallyTryToConnectToAddress()

            is OnSendImageClicked -> viewModelScope.launch(ioDispatcher) {
                sendImage(imageUri = event.uri, address = params.address).collect { state ->
                    if (state == SENDING) {
                        _viewEffect.trySend(ScrollToBottom)
                    }
                }
            }

            is OnStartRecordingVoiceClicked ->
                voiceRecorder.startRecording()
                .fold(
                    ifLeft = { failure -> _viewEffect.trySend(Error( "TODO")) },
                    ifRight = { filePath ->
                        voiceRecordingFilePath = filePath
                        isRecordingVoice.update { true }
                        inputMode.update { VOICE }
                    },
                )

            is OnStopRecordingVoiceClicked -> {
                voiceRecorder.stopRecording()
                isRecordingVoice.update { false }
            }

            is OnDeleteVoiceRecordingClicked -> {
                voiceRecorder.stopRecording()
                voiceRecordingFilePath?.let { path -> deleteAudio(path) }
                voiceRecordingFilePath = null
                isRecordingVoice.update { false }
                inputMode.update { TEXT }
            }

            is OnPausePlayingAudioMessage -> audioPlayer.pause()
                .onRight { audioMessagePlaying.update { current -> current?.copy(isPlaying = false) } }

            is ChatScreenEvent.OnPlayAudioMessage -> viewModelScope.launch(ioDispatcher) {
                val currentPlayingMessage = audioMessagePlaying.value
                val settingUp = if (currentPlayingMessage == null || currentPlayingMessage.id != event.message.id) {
                    audioPlayer.stop()
                    audioPlayer.setDataSource(event.message.filePath)
                } else {
                    Either.Right(Unit)
                }
                settingUp.flatMap { audioPlayer.play() }.fold(
                    { failure -> _viewEffect.trySend(Error(failure.errorMessage)) },
                    { audioMessagePlaying.update { event.message.copy(isPlaying = true) } },
                )
            }
        }
    }

    private fun startServerAndPeriodicallyTryToConnectToAddress() {
        isTryingToConnect.update { true }
        viewModelScope.launch(ioDispatcher) {
            connectToKnownContact.invoke(params.address)
            isTryingToConnect.update { false }
        }
    }

    override fun onCleared() {
        audioPlayer.release()
        voiceRecorder.release()
    }

    private fun areSameDate(timestamp1: Long, timestamp2: Long): Boolean {
        val calendar1 = Calendar.getInstance().apply {
            timeInMillis = timestamp1
        }
        val calendar2 = Calendar.getInstance().apply {
            timeInMillis = timestamp2
        }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar2.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
            calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(params: ChatViewModelParams): ChatViewModel
    }
}
