package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import arrow.core.Either
import arrow.core.flatMap
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.extensions.combine
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.chat.models.SendState.SENDING
import com.karlom.bluetoothmessagingapp.domain.audio.CreateAudioFile
import com.karlom.bluetoothmessagingapp.domain.audio.DeleteAudioFile
import com.karlom.bluetoothmessagingapp.domain.audio.GetAudioPlayer
import com.karlom.bluetoothmessagingapp.domain.audio.GetVoiceRecorder
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.ConnectToServer
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.GetMessages
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendAudio
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendImage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartChatServerAndWaitForConnection
import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlom.bluetoothmessagingapp.domain.connection.usecase.GetConnectedDevicesNotifier
import com.karlom.bluetoothmessagingapp.domain.connection.usecase.IsConnectedTo
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.ChatMessageMapper
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.DateIndicatorMapper
import com.karlom.bluetoothmessagingapp.feature.chat.mappers.SeparatorMapper
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect
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
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatViewModelParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@HiltViewModel(assistedFactory = ChatViewModel.ChatViewModelFactory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted private val params: ChatViewModelParams,
    private val getMessages: GetMessages,
    private val sendMessage: SendMessage,
    private val isConnectedTo: IsConnectedTo,
    private val sendImage: SendImage,
    private val getAudioPlayer: GetAudioPlayer,
    private val getConnectionStateNotifier: GetConnectedDevicesNotifier,
    private val startChatServerAndWaitForConnection: StartChatServerAndWaitForConnection,
    private val getVoiceRecorder: GetVoiceRecorder,
    private val createAudioFile: CreateAudioFile,
    private val deleteAudioFile: DeleteAudioFile,
    private val sendAudio: SendAudio,
    private val connectToServer: ConnectToServer,
    private val chatMessageMapper: ChatMessageMapper,
    private val dateIndicatorMapper: DateIndicatorMapper,
    private val separatorMapper: SeparatorMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
                } else if (after == null) {
                    ChatItem.StartOfMessagingIndicator(params.name)
                } else {
                    null
                }
            }
            .insertSeparators { before, after -> separatorMapper.map(before = before, after = after) }
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
                showConnectToDeviceButton.update { connectedDevices.firstOrNull { it.address == params.address } == null }
            }
        }
    }

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
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

            is OnPausePlayingAudioMessage -> audioPlayer.pause().onRight { audioMessagePlaying.update { current -> current?.copy(isPlaying = false) } }

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
            val waitForClientJob = async { startChatServerAndWaitForConnection() }
            val tryToConnectJob = async {
                var connectToServer: Either<Failure.ErrorMessage, Connection>? = null
                repeat(NUMBER_OF_RETRIES_WHEN_CONNECTING) { timesRun ->
                    connectToServer = connectToServer(params.address).fold(
                        { failure ->
                            if (timesRun < NUMBER_OF_RETRIES_WHEN_CONNECTING - 1) {
                                delay(RETRY_DELAY_MILLIS)
                                null
                            } else {
                                Either.Left(failure)
                            }
                        },
                        { connection -> Either.Right(connection) },
                    )
                    connectToServer?.let { return@repeat }
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
        audioPlayer.releaseMediaPlayer()
        voiceRecorder.relase()
    }

    private fun areSameDate(timestamp1: Long, timestamp2: Long): Boolean {
        val calendar1 = Calendar.Builder().setInstant(timestamp1).build()
        val calendar2 = Calendar.Builder().setInstant(timestamp2).build()
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar2.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
            calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(params: ChatViewModelParams): ChatViewModel
    }
}
