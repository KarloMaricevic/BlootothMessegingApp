package com.karlomaricevic.bluetoothmessagingapp.feature.chat

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.rememberPermissionState
import com.karlomaricevic.bluetoothmessagingapp.designsystem.gray500
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.AudioChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ChatScreenToolbar
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ContactIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.ImageChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.*
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.*
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEffect.*
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatViewModelParams
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.SimplifiedSimpleLazyColumn

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
                is Error -> scaffoldState.showSnackbar(chatEffect.errorMessage)
                is ScrollToBottom -> listState.animateScrollToItem(0)
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        ChatScreenToolbar(
            contactName = contactName,
            onInteraction = viewModel::onEvent,
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
                            onInteraction = viewModel::onEvent
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
