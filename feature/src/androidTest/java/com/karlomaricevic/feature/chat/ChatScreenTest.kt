package com.karlomaricevic.feature.chat

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.ChatScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Text
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.MICROPHONE_BUTTON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.DevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val permissionsHandler = mockk<DevicePermissionsHandler>(relaxed = true)
    private val imageResolver = mockk<ImageResolver<ChatScreenImageKeys>>()
    private val stringResolver = mockk<StringResolver<ChatScreenStringKeys>>()
    private val onEvent = mockk<(ChatScreenEvent) -> Unit>(relaxed = true)

    @Before
    fun setUp() {
        every { onEvent(any()) } just Runs
        every { imageResolver.getImage(any()) } returns ImageResource.Mock
        every { stringResolver.getString(any()) } returns ""
    }

    @Test
    fun shows_textMessage_whenMessageListHasText() {
        val textMessage = "Hello!"
        val state = ChatScreenState(
            showConnectToDeviceButton = true,
            messages = listOf(
                Text(
                    id = 1L,
                    message = textMessage,
                    isFromMe = true,
                    state = SENT,
                    timestamp = 0
                )
            ),
        )

        composeTestRule.setContent {
            ChatScreen(
                state = state,
                onEvent = onEvent,
                effectFlow = flowOf(),
                contactName = "",
                scaffoldState = SnackbarHostState(),
                permissionHandler = permissionsHandler,
                imageResolver = imageResolver,
                stringResolver = stringResolver,
            )
        }

        composeTestRule.onNodeWithText(textMessage).assertIsDisplayed()
    }

    @Test
    fun showsValidMessage_whenAudioMessageNotPlaying() {
        val audioMessage = Audio(
            id = 1L,
            filePath = "",
            isFromMe = true,
            totalTime = "1:32",
            state = SENT,
            isPlaying = false,
            timestamp = 0L,
        )
        val state = ChatScreenState(
            showConnectToDeviceButton = true,
            messages = listOf(audioMessage),
        )

        composeTestRule.setContent {
            ChatScreen(
                state = state,
                onEvent = onEvent,
                effectFlow = flowOf(),
                contactName = "",
                scaffoldState = SnackbarHostState(),
                permissionHandler = permissionsHandler,
                imageResolver = imageResolver,
                stringResolver = stringResolver,
            )
        }

        verify { imageResolver.getImage(ChatScreenImageKeys.PLAY_ICON) }
        verify(exactly = 0) { imageResolver.getImage(ChatScreenImageKeys.PAUSE_ICON) }
         composeTestRule.onNodeWithText(audioMessage.totalTime).assertIsDisplayed()
    }

    @Test
    fun showsValidMessage_whenAudioMessagePlaying() {
        val audioMessage = Audio(
            id = 1L,
            filePath = "",
            isFromMe = true,
            totalTime = "1:32",
            state = SENT,
            isPlaying = true,
            timestamp = 0L,
        )
        val state = ChatScreenState(
            showConnectToDeviceButton = true,
            messages = listOf(audioMessage),
        )

        composeTestRule.setContent {
            ChatScreen(
                state = state,
                onEvent = onEvent,
                effectFlow = flowOf(),
                contactName = "",
                scaffoldState = SnackbarHostState(),
                permissionHandler = permissionsHandler,
                imageResolver = imageResolver,
                stringResolver = stringResolver,
            )
        }

        verify { imageResolver.getImage(ChatScreenImageKeys.PAUSE_ICON) }
        verify(exactly = 0) { imageResolver.getImage(ChatScreenImageKeys.PLAY_ICON) }
        composeTestRule.onNodeWithText(audioMessage.totalTime).assertIsDisplayed()
    }

    // TODO Add image message tests

    @Test
    fun clickingMicrophone_requestsPermission() {
        every { stringResolver.getString(MICROPHONE_BUTTON_CONTENT_DESCRIPTION) } returns "Microphone"
        val state = ChatScreenState(showConnectToDeviceButton = false)

        composeTestRule.setContent {
            ChatScreen(
                state = state,
                onEvent = onEvent,
                effectFlow = flowOf(),
                contactName = "",
                scaffoldState = SnackbarHostState(),
                permissionHandler = permissionsHandler,
            )
        }

        composeTestRule.onNodeWithContentDescription(stringResolver.getString(
            MICROPHONE_BUTTON_CONTENT_DESCRIPTION
        )).performClick()

        verify { permissionsHandler.requestVoicePermission(any()) }
    }
}
