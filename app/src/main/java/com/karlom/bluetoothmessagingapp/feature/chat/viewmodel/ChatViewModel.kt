package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertFooterItem
import androidx.paging.insertSeparators
import androidx.paging.map
import arrow.core.Either
import arrow.core.flatMap
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.extensions.combine
import com.karlom.bluetoothmessagingapp.data.chat.models.SendState.SENDING
import com.karlom.bluetoothmessagingapp.domain.audio.CreateAudioFile
import com.karlom.bluetoothmessagingapp.domain.audio.DeleteAudioFile
import com.karlom.bluetoothmessagingapp.domain.audio.GetAudioPlayer
import com.karlom.bluetoothmessagingapp.domain.audio.GetVoiceRecorder
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.ConnectToKnownUser
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.GetMessages
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendAudio
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendImage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.domain.connection.usecase.GetConnectedDeviceNotifier
import com.karlom.bluetoothmessagingapp.domain.connection.usecase.IsConnectedTo
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.ChatMessageMapper
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.DateIndicatorMapper
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.SeparatorMapper
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.StartOfMessagingIndicator
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnBackClicked
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
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatViewModelParams
import com.karlom.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    private val sendMessage: SendMessage,
    private val isConnectedTo: IsConnectedTo,
    private val sendImage: SendImage,
    private val getAudioPlayer: GetAudioPlayer,
    private val getConnectionStateNotifier: GetConnectedDeviceNotifier,
    private val getVoiceRecorder: GetVoiceRecorder,
    private val createAudioFile: CreateAudioFile,
    private val deleteAudioFile: DeleteAudioFile,
    private val sendAudio: SendAudio,
    private val connectToKnownUser: ConnectToKnownUser,
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
    private val messages = MutableStateFlow(getMessages(params.address).map { page ->
        page
            .map { message -> chatMessageMapper.map(message) }
            .insertSeparators { before, after ->
                if (before != null && after != null) {
                    if (areSameDate(before.timestamp, after.timestamp)) {
                        null
                    } else {
                        dateIndicatorMapper.map(before)
                    }
                } else if (after == null && before != null) {
                    dateIndicatorMapper.map(before)
                } else {
                    null
                }
            }
            .insertSeparators { before, after -> separatorMapper.map(before = before, after = after) }
            .insertFooterItem(item = StartOfMessagingIndicator(params.name))
    }.cachedIn(viewModelScope)
        .combine(audioMessagePlaying) { page, audioPlaying ->
            page.map { message ->
                if (audioPlaying != null && message is Audio && message.id == audioPlaying.id) {
                    audioPlaying
                } else {
                    message
                }
            }
        })

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
                    TEXT -> sendMessage(message = textToSend.value, address = params.address)

                    VOICE -> voiceRecordingFilePath?.let { voiceRecordingFilePath ->
                        sendAudio(imagePath = voiceRecordingFilePath, address = params.address)
                    }
                }?.collect { state ->
                    if (state == SENDING) {
                        _viewEffect.trySend(ChatScreenEffect.ScrollToBottom)
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
                        _viewEffect.trySend(ChatScreenEffect.ScrollToBottom)
                    }
                }
            }

            is OnStartRecordingVoiceClicked -> createAudioFile(UUID.randomUUID().toString())
                .flatMap { fileName -> voiceRecorder.startRecording(fileName) }
                .fold(
                    { failure -> _viewEffect.trySend(ChatScreenEffect.Error(failure.errorMessage)) },
                    { filePath ->
                        voiceRecordingFilePath = filePath
                        isRecordingVoice.update { true }
                        inputMode.update { VOICE }
                    },
                )

            is OnStopRecordingVoiceClicked -> {
                voiceRecorder.endRecording()
                isRecordingVoice.update { false }
            }

            is OnDeleteVoiceRecordingClicked -> {
                voiceRecorder.endRecording()
                voiceRecordingFilePath?.let { filePath -> deleteAudioFile(filePath) }
                voiceRecordingFilePath = null
                isRecordingVoice.update { false }
                inputMode.update { TEXT }
            }

            is OnPausePlayingAudioMessage -> audioPlayer.pause()
                .onRight { audioMessagePlaying.update { current -> current?.copy(isPlaying = false) } }

            is OnPlayAudioMessage -> viewModelScope.launch(ioDispatcher) {
                val currentPlayingMessage = audioMessagePlaying.value
                val settingUp = if (currentPlayingMessage == null || currentPlayingMessage.id != event.message.id) {
                    audioPlayer.stop()
                    audioPlayer.setDataSource(event.message.filePath)
                } else {
                    Either.Right(Unit)
                }
                settingUp.flatMap { audioPlayer.play() }.fold(
                    { failure -> _viewEffect.trySend(ChatScreenEffect.Error(failure.errorMessage)) },
                    { audioMessagePlaying.update { event.message.copy(isPlaying = true) } },
                )
            }
        }
    }

    private fun startServerAndPeriodicallyTryToConnectToAddress() {
        isTryingToConnect.update { true }
        viewModelScope.launch(ioDispatcher) {
            connectToKnownUser.invoke(params.address)
            isTryingToConnect.update { false }
        }
    }

    override fun onCleared() {
        audioPlayer.releaseMediaPlayer()
        voiceRecorder.relase()
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
