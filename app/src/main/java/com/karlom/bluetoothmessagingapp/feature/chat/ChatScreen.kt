package com.karlom.bluetoothmessagingapp.feature.chat

import android.Manifest
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberPermissionState
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray500
import com.karlom.bluetoothmessagingapp.feature.chat.components.AudioChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatScreenToolbar
import com.karlom.bluetoothmessagingapp.feature.chat.components.ConnectToButton
import com.karlom.bluetoothmessagingapp.feature.chat.components.ContactIndicator
import com.karlom.bluetoothmessagingapp.feature.chat.components.ImageChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Image
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Text
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.DateIndicator
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.StartOfMessagingIndicator
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatViewModelParams
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ChatScreen(
    contactName: String,
    address: String,
    scaffoldState: SnackbarHostState,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ChatViewModel, ChatViewModel.ChatViewModelFactory> { factory ->
        factory.create((ChatViewModelParams(name = contactName, address = address)))
    }
    val state by viewModel.state.collectAsState()
    val messages = state.messages.collectAsLazyPagingItems()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onEvent(
                OnSendImageClicked(
                    result.data?.data?.toString() ?: Uri.EMPTY.toString()
                )
            )
        }
    }
    val voicePermissionLauncher = rememberPermissionState(
        permission = Manifest.permission.RECORD_AUDIO,
    ) { permissionAccepted ->
        if (permissionAccepted) {
            viewModel.onEvent(OnStartRecordingVoiceClicked)
        }
    }
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = viewModel.viewEffect) {
        viewModel.viewEffect.collect { chatEffect ->
            when (chatEffect) {
                is ChatScreenEffect.Error -> scaffoldState.showSnackbar(chatEffect.errorMessage)
                is ChatScreenEffect.ScrollToBottom -> listState.animateScrollToItem(0)
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        ChatScreenToolbar(
            contactName = contactName,
            onInteraction = viewModel::onEvent,
        )
        Box(Modifier.weight(weight = 1f, fill = true)) {
            SimpleLazyColumn(
                items = messages,
                key = {
                    when (this) {
                        is ChatItem.MessageSeparator -> id
                        is StartOfMessagingIndicator -> "startOfMessagingIndicator"
                        is DateIndicator -> date
                        is ChatMessage -> "$id"
                    }
                },
                uiItemBuilder = { message ->
                    when (message) {
                        is ChatItem.MessageSeparator -> Box(Modifier.height(message.value.dp))
                        is StartOfMessagingIndicator -> ContactIndicator(message.name)
                        is Text -> TextChatBox(message)
                        is Image -> ImageChatBox(message)
                        is Audio -> AudioChatBox(
                            message = message,
                            onInteraction = viewModel::onEvent,
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
                state = listState,
                reverseLayout = true,
                noItemsItem = { },
                modifier = Modifier.fillMaxSize()
            )
            if (state.showConnectToDeviceButton) {
                ConnectToButton(
                    isConnecting = state.isTryingToConnect,
                    onClick = if (state.isTryingToConnect) {
                        null
                    } else {
                        { viewModel.onEvent(OnConnectClicked) }
                    },
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
        AnimatedVisibility(
            visible = !state.showConnectToDeviceButton,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            ChatInputFiled(
                text = state.textToSend,
                onInteraction = viewModel::onEvent,
                inputMode = state.inputMode,
                isRecording = state.isRecordingVoice,
                onGalleryClicked = {
                    val intent = (Intent(Intent.ACTION_PICK).apply { type = "image/*" })
                    if (context.packageManager.queryIntentActivities(intent, 0).size > 0) {
                        galleryLauncher.launch(intent)
                    }
                },
                onMicrophoneClicked = { voicePermissionLauncher.launchPermissionRequest() }
            )
        }
    }
}
