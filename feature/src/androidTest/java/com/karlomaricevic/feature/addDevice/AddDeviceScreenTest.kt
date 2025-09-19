package com.karlomaricevic.feature.addDevice

import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.ADD_DEVICE_SCREEN_SWITCH_TEST_TAG
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.AddDeviceScreen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.*
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.DevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class AddDeviceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val permissionsHandler = mockk<DevicePermissionsHandler>(relaxed = true)
    private val stringResolver = mockk<StringResolver<AddDeviceScreenStringKeys>>()
    private val imageResolver = mockk<ImageResolver<AddDeviceScreenImageKeys>>()

    private val onEvent = mockk<(AddDeviceScreenEvent) -> Unit>()

    @Before
    fun setUp() {
        every { stringResolver.getString(any()) } returns ""
        every { imageResolver.getImage(any()) } returns ImageResource.Mock
        every { onEvent(any()) } just Runs
    }

    @Test
    fun shows_listOfDevices_whenBluetoothDevicesAvailable() {
        val device1Name = "Device 1"
        val device2Name = "Device 2"
        val devices = listOf(
            Connection(name = device1Name, address = "01:23:45:67:89:AB"),
            Connection(name = device2Name, address = "F0:99:B6:12:34:56"),
        )
        val state = AddDeviceScreenState(showStartSearchMessage = false, bluetoothDevices = devices)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = {},
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(device1Name).assertIsDisplayed()
        composeTestRule.onNodeWithText(device2Name).assertIsDisplayed()
    }

    @Test
    fun shows_noDevicesMessage_whenBluetoothDevicesListIsEmpty() {
        val noDevices = "noDevices"
        val state = AddDeviceScreenState(showStartSearchMessage = false, bluetoothDevices = emptyList())
        every { stringResolver.getString(NO_DEVICES_NEARBY) } returns noDevices

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = {},
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(noDevices).assertIsDisplayed()
    }


    @Test
    fun shows_startSearchButton_whenStartSearchMessageVisible() {
        val startSearch = "startSearch"
        val state = AddDeviceScreenState(showStartSearchMessage = true)
        every { stringResolver.getString(START_SEARCH_BUTTON) } returns startSearch

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = {},
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(startSearch).assertIsDisplayed()
    }

    @Test
    fun clickingDevice_sendsOnDeviceClickedEvent() {
        val deviceName = "Device X"
        val deviceAddress = "01:23:45:67:89:AB"
        val devices = listOf(Connection(name = deviceName, address = deviceAddress))
        val state = AddDeviceScreenState(showStartSearchMessage = false, bluetoothDevices = devices)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(deviceName).performClick()

        verify { onEvent(OnDeviceClicked(deviceAddress)) }
    }

    @Test
    fun clickingStartSearch_startScanPermission() {
        val startSearchText = "Start search"
        every { stringResolver.getString(START_SEARCH_BUTTON) } returns startSearchText
        val state = AddDeviceScreenState(showStartSearchMessage = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.onNodeWithText(startSearchText).performClick()

        verify { permissionsHandler.requestScanPermissions(any()) }
    }

    @Test
    fun clickingStartSearchWithPermissionAccepted_sendsOnScanForDevicesClickedEvent() {
        val startSearchText = "Start search"
        every { stringResolver.getString(START_SEARCH_BUTTON) } returns startSearchText
        every { permissionsHandler.requestScanPermissions(any()) } answers {
            val callback = firstArg<(Boolean) -> Unit>()
            callback(true)
        }
        val state = AddDeviceScreenState(showStartSearchMessage = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.onNodeWithText(startSearchText).performClick()

        verify { onEvent(OnScanForDevicesClicked) }
    }

    @Test
    fun clickingStartSearchWithPermissionDeclined_dosentSendOnScanForDevicesClickedEvent() {
        val startSearchText = "Start search"
        every { stringResolver.getString(START_SEARCH_BUTTON) } returns startSearchText
        every { permissionsHandler.requestScanPermissions(any()) } answers {
            val callback = firstArg<(Boolean) -> Unit>()
            callback(false)
        }
        val state = AddDeviceScreenState(showStartSearchMessage = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
            )
        }
        composeTestRule.onNodeWithText(startSearchText).performClick()

        verify(exactly = 0) { onEvent(OnScanForDevicesClicked) }
    }

    @Test
    fun clickingBackIcon_sendsOnBackClickedEvent() {
        val backContentDescription = "back"
        every { stringResolver.getString(BACK_CONTENT_DESCRIPTION) } returns backContentDescription

        composeTestRule.setContent {
            AddDeviceScreen(
                state = AddDeviceScreenState(),
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver
            )
        }
        composeTestRule.onNodeWithContentDescription(backContentDescription).performClick()

        verify { onEvent(OnBackClicked) }
    }

    @Test
    fun clickingDiscoverableSwitch_startDiscoverablePermission() {
        composeTestRule.setContent {
            AddDeviceScreen(
                state = AddDeviceScreenState(isDiscoverable = false),
                onEvent = onEvent,
                permissionHandler = permissionsHandler
            )
        }
        composeTestRule.onNodeWithTag(ADD_DEVICE_SCREEN_SWITCH_TEST_TAG).performClick()

        verify(exactly = 1) { permissionsHandler.requestDiscoverable(any()) }
    }

    @Test
    fun clickingDiscoverableSwitch_grantedPermission_sendsOnDiscoverableSwitchChecked() {
        every { permissionsHandler.requestDiscoverable(any()) } answers {
            val callback = arg<(Boolean) -> Unit>(0)
            callback(true)
        }

        composeTestRule.setContent {
            AddDeviceScreen(
                state = AddDeviceScreenState(isDiscoverable = false),
                onEvent = onEvent,
                permissionHandler = permissionsHandler
            )
        }
        composeTestRule.onNodeWithTag(ADD_DEVICE_SCREEN_SWITCH_TEST_TAG).performClick()

        verify { onEvent(OnDiscoverableSwitchChecked) }
    }

    @Test
    fun clickingDiscoverableSwitch_deniedPermission_doesNotSendEvent() {
        every { permissionsHandler.requestDiscoverable(any()) } answers {
            val callback = arg<(Boolean) -> Unit>(0)
            callback(false)
        }

        composeTestRule.setContent {
            AddDeviceScreen(
                state = AddDeviceScreenState(),
                onEvent = onEvent,
                permissionHandler = permissionsHandler
            )
        }
        composeTestRule.onNodeWithTag(ADD_DEVICE_SCREEN_SWITCH_TEST_TAG).performClick()

        verify(exactly = 0) { onEvent(OnDiscoverableSwitchChecked) }
    }

    @Test
    fun shows_makeDeviceVisibleErrorDialog_whenShowMakeDeviceVisibleErrorIsTrue() {
        val errorTitle = "Device not visible"
        val errorMessage = "We couldn’t make your device discoverable. Please check Bluetooth settings and try again."
        val confirmButton = "OK"
        every { stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_TITLE) } returns errorTitle
        every { stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_MESSAGE) } returns errorMessage
        every { stringResolver.getString(DIALOG_CONFIRM_BUTTON) } returns confirmButton
        val state = AddDeviceScreenState(showMakeDeviceVisibleError = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = {},
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(errorTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(confirmButton).assertIsDisplayed()
    }

    @Test
    fun shows_connectingToDeviceErrorDialog_whenShowConnectingToDeviceErrorTrue() {
        val errorTitle = "Connection failed"
        val errorMessage = "Couldn’t connect to the selected device. Please try again."
        val confirmButton = "OK"
        every { stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_TITLE) } returns errorTitle
        every { stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_MESSAGE) } returns errorMessage
        every { stringResolver.getString(DIALOG_CONFIRM_BUTTON) } returns confirmButton
        val state = AddDeviceScreenState(showConnectingToDeviceError = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = {},
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(errorTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(confirmButton).assertIsDisplayed()
    }

    @Test
    fun clicking_makeDeviceVisibleErrorDialogConfirm_sendsDismissEvent() {
        val confirmButton = "OK"
        every { stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_TITLE) } returns "Device not visible"
        every { stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_MESSAGE) } returns "Message"
        every { stringResolver.getString(DIALOG_CONFIRM_BUTTON) } returns confirmButton
        val state = AddDeviceScreenState(showMakeDeviceVisibleError = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(confirmButton).performClick()

        verify { onEvent(OnDismissErrorDialogClicked) }
    }

    @Test
    fun clicking_connectingToDeviceErrorDialogConfirm_sendsDismissEvent() {
       val confirmButton = "OK"
        every { stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_TITLE) } returns "Connection failed"
        every { stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_MESSAGE) } returns "Message"
        every { stringResolver.getString(DIALOG_CONFIRM_BUTTON) } returns confirmButton
        val state = AddDeviceScreenState(showConnectingToDeviceError = true)

        composeTestRule.setContent {
            AddDeviceScreen(
                state = state,
                onEvent = onEvent,
                permissionHandler = permissionsHandler,
                stringResolver = stringResolver,
                imageResolver = imageResolver
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(confirmButton).performClick()

        verify { onEvent(OnDismissErrorDialogClicked) }
    }
}
