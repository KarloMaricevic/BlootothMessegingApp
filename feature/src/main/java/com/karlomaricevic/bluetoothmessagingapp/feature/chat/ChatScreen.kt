package com.karlomaricevic.bluetoothmessagingapp.feature.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.gray500
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.NOT_SENT
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENDING
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.AudioChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ChatScreenToolbar
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ContactIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ImageChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Image
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Text
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.DateIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.MessageSeparator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.StartOfMessagingIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect.Error
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect.ScrollToBottom
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatStringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.DevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.SimplifiedSimpleLazyColumn
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.rememberAndroidDevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ChatScreen(
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit,
    effectFlow: Flow<ChatScreenEffect>,
    stringResolver: StringResolver<ChatScreenStringKeys> = ChatStringResolver(LocalContext.current),
    imageResolver: ImageResolver<ChatScreenImageKeys> = ChatImageResolver(),
    contactName: String,
    permissionHandler: DevicePermissionsHandler = rememberAndroidDevicePermissionsHandler(
        context = LocalContext.current,
        activity = LocalContext.current as Activity
    ),
    scaffoldState: SnackbarHostState,
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onEvent(
                OnSendImageClicked(
                    result.data?.data?.toString() ?: Uri.EMPTY.toString()
                )
            )
        }
    }
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = effectFlow) {
        effectFlow.collect { chatEffect ->
            when (chatEffect) {
                is Error -> scaffoldState.showSnackbar(chatEffect.errorMessage)
                is ScrollToBottom -> listState.animateScrollToItem(0)
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        ChatScreenToolbar(
            contactName = contactName,
            onInteraction = onEvent,
            stringResolver = stringResolver,
            imageResolver = imageResolver,
        )
        Box(Modifier.weight(weight = 1f, fill = true)) {
            SimplifiedSimpleLazyColumn(
                items = state.messages,
                reverseLayout = true,
                modifier = Modifier.fillMaxSize(),
                key = { message ->
                    when (message) {
                        is MessageSeparator -> "separator_${message.id}"
                        is StartOfMessagingIndicator -> "start_indicator"
                        is DateIndicator -> "date_${message.date.hashCode()}"
                        is ChatMessage -> "msg_${message.id}"
                    }
                },
                uiItemBuilder = { message ->
                    when (message) {
                        is MessageSeparator -> Box(Modifier.height(message.value.dp))
                        is StartOfMessagingIndicator -> ContactIndicator(message.name)
                        is Text -> TextChatBox(message)
                        is Image -> ImageChatBox(message)
                        is Audio -> AudioChatBox(
                            message = message,
                            onInteraction = onEvent,
                            imageResolver = imageResolver,
                            stringResolver = stringResolver,
                        )

                        is DateIndicator -> Text(
                            text = message.date,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = gray500,
                        )
                    }
                },
                noItemsItem = {},
            )
        }
        AnimatedVisibility(
            visible = !state.showConnectToDeviceButton,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            ChatInputFiled(
                text = state.textToSend,
                onInteraction = onEvent,
                inputMode = state.inputMode,
                isRecording = state.isRecordingVoice,
                onGalleryClicked = {
                    val intent = (Intent(Intent.ACTION_PICK).apply { type = "image/*" })
                    if (context.packageManager.queryIntentActivities(intent, 0).size > 0) {
                        galleryLauncher.launch(intent)
                    }
                },
                onMicrophoneClicked = {
                    permissionHandler.requestVoicePermission { granted ->
                        if (granted) {
                            onEvent(OnStartRecordingVoiceClicked)
                        }
                    }
                },
                imageResolver = imageResolver,
                stringResolver = stringResolver,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenConnectedWithMessagesPreview() {
    BluetoothMessagingAppTheme {
        ChatScreen(
            state = ChatScreenState(
                messages = createMessageListForPreview(),
                showConnectToDeviceButton = false,
                textToSend = List(3) { "New message to send!" }.joinToString(" ")
            ),
            onEvent = {},
            effectFlow = flowOf(),
            contactName = "John Smith",
            scaffoldState = SnackbarHostState(),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {}
                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {}
                override fun enableBluetooth(onResult: (Boolean) -> Unit) {}
                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {}
                override fun requestVoicePermission(onResult: (Boolean) -> Unit) {}
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenNotConnectedWithMessagesPreview() {
    BluetoothMessagingAppTheme {
        ChatScreen(
            state = ChatScreenState(
                messages = createMessageListForPreview(),
                showConnectToDeviceButton = true,
            ),
            onEvent = {},
            effectFlow = flowOf(),
            contactName = "John Smith",
            scaffoldState = SnackbarHostState(),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {}
                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {}
                override fun enableBluetooth(onResult: (Boolean) -> Unit) {}
                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {}
                override fun requestVoicePermission(onResult: (Boolean) -> Unit) {}
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenConnectedWithVoiceInputPreview() {
    BluetoothMessagingAppTheme {
        ChatScreen(
            state = ChatScreenState(
                messages = createMessageListForPreview(),
                showConnectToDeviceButton = false,
                inputMode = VOICE,
                isRecordingVoice = true,
            ),
            onEvent = {},
            effectFlow = flowOf(),
            contactName = "John Smith",
            scaffoldState = SnackbarHostState(),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {}
                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {}
                override fun enableBluetooth(onResult: (Boolean) -> Unit) {}
                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {}
                override fun requestVoicePermission(onResult: (Boolean) -> Unit) {}
            }
        )
    }
}

private fun createMessageListForPreview() = listOf(
    Audio(
        id = 8L,
        filePath = "",
        totalTime = "10",
        isPlaying = false,
        currentTime = "1",
        isFromMe = true,
        state = NOT_SENT,
        timestamp = 10111212
    ),
    Audio(
        id = 7L,
        filePath = "",
        totalTime = "12",
        isPlaying = false,
        currentTime = "1",
        isFromMe = true,
        state = SENDING,
        timestamp = 10111212
    ),
    Audio(
        id = 6L,
        filePath = "",
        totalTime = "10",
        isPlaying = true,
        currentTime = "1",
        isFromMe = false,
        state = SENT,
        timestamp = 10111212
    ),
    Text(
        id = 5L,
        message = "This message failed to send",
        isFromMe = true,
        state = NOT_SENT,
        timestamp = 10111212,
    ),
    Text(
        id = 4L,
        message = "This message is sending",
        isFromMe = true,
        state = SENDING,
        timestamp = 10111212,
    ),
    Text(
        id = 3L,
        message = List(10) { "This is long message."}.joinToString(" "),
        isFromMe = true,
        state = SENT,
        timestamp = 10111212,
    ),
    Text(
        id = 11L,
        message = List(10) { "This is long message."}.joinToString(" "),
        isFromMe = false,
        state = SENT,
        timestamp = 10111212,
    ),
    Text(
        id = 2L,
        message = "Hello my friend!",
        isFromMe = true,
        state = SENT,
        timestamp = 10111212,
    ),
    Text(
        id = 1L,
        message = "Hello!",
        isFromMe = false,
        state = SENT,
        timestamp = 10111212,
    ),
)

