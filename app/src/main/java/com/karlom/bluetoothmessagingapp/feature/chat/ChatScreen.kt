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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberPermissionState
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.ConnectToButton
import com.karlom.bluetoothmessagingapp.feature.chat.components.ImageChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStartRecordingVoiceClicked
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ChatScreen(address: String) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ChatViewModel, ChatViewModel.ChatViewModelFactory> { factory ->
        factory.create((address))
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
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(weight = 1f, fill = true)) {
            SimpleLazyColumn(
                items = messages,
                key = { id },
                uiItemBuilder = { message ->
                    when (message) {
                        is Message.TextMessage -> TextChatBox(message)
                        is Message.ImageMessage -> ImageChatBox(message)
                        else -> { // TODO
                        }
                    }
                },
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
