package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.gray500
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.components.BluetoothDeviceItem
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.components.ErrorDialog
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnBackClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDismissErrorDialogClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.AddDeviceImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.AddDeviceStringsResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.BACK_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.CONNECTING_TO_DEVICE_ERROR_MESSAGE
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.CONNECTING_TO_DEVICE_ERROR_TITLE
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.DIALOG_CONFIRM_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.MAKE_DEVICE_VISIBLE_ERROR_MESSAGE
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.MAKE_DEVICE_VISIBLE_ERROR_TITLE
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.NO_DEVICES_NEARBY
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.START_SEARCH_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.DevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.MultiplatformIcon
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.rememberAndroidDevicePermissionsHandler
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

const val ADD_DEVICE_SCREEN_SWITCH_TEST_TAG = "add_device_screen_switch_test_tag"

@Composable
fun AddDeviceScreen(
    state: AddDeviceScreenState,
    onEvent: (AddDeviceScreenEvent) -> Unit,
    permissionHandler: DevicePermissionsHandler = rememberAndroidDevicePermissionsHandler(
        context = LocalContext.current,
        activity = LocalContext.current as Activity
    ),
    stringResolver: StringResolver<AddDeviceScreenStringKeys> = AddDeviceStringsResolver(LocalContext.current),
    imageResolver: ImageResolver<AddDeviceScreenImageKeys> = AddDeviceImageResolver()
) {
    if (state.showMakeDeviceVisibleError) {
        ErrorDialog(
            title = stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_TITLE),
            message = stringResolver.getString(MAKE_DEVICE_VISIBLE_ERROR_MESSAGE),
            confirmButtonText = stringResolver.getString(DIALOG_CONFIRM_BUTTON),
            onDismiss = { onEvent(OnDismissErrorDialogClicked) },
        )
    }
    if (state.showConnectingToDeviceError) {
        ErrorDialog(
            title = stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_TITLE),
            message = stringResolver.getString(CONNECTING_TO_DEVICE_ERROR_MESSAGE),
            confirmButtonText = stringResolver.getString(DIALOG_CONFIRM_BUTTON),
            onDismiss = { onEvent(OnDismissErrorDialogClicked) },
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(bottom = 4.dp),
        ) {
            MultiplatformIcon(
                imageKey = AddDeviceScreenImageKeys.BACK_ICON,
                imageResolver = imageResolver,
                contentDescription = stringResolver.getString(BACK_CONTENT_DESCRIPTION),
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 8.dp)
                    .clip(CircleShape)
                    .clickable { onEvent(OnBackClicked) }
                    .padding(12.dp)
                    .size(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResolver.getString(AddDeviceScreenStringKeys.MAKE_DEVICE_VISIBLE_MESSAGE),
                modifier = Modifier.weight(1f, true),
                color = gray500,
                style = MaterialTheme.typography.titleMedium,
            )
            Switch(
                checked = state.isDiscoverable,
                enabled = !state.isDiscoverable,
                modifier = Modifier
                    .testTag(ADD_DEVICE_SCREEN_SWITCH_TEST_TAG)
                    .padding(end = 16.dp)
                    .size(height = 20.dp, width = 16.dp),
                onCheckedChange = {
                    permissionHandler.requestDiscoverable { granted ->
                        if (granted) onEvent(OnDiscoverableSwitchChecked)
                    }
                },
                colors = SwitchDefaults.colors(
                    disabledCheckedBorderColor = blue,
                    disabledCheckedThumbColor = white,
                    disabledCheckedIconColor = blue,
                    disabledCheckedTrackColor = blue,
                ),
            )
        }
        Text(
            text = stringResolver.getString(AddDeviceScreenStringKeys.BLUETOOTH_DEVICES_TITLE),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 8.dp, top = 24.dp)
        )
        if (state.showStartSearchMessage) {
            Box(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        permissionHandler.requestScanPermissions { granted ->
                            if (granted) onEvent(OnScanForDevicesClicked)
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = stringResolver.getString(START_SEARCH_BUTTON))
                }
            }
        } else if (state.bluetoothDevices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResolver.getString(NO_DEVICES_NEARBY),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.bluetoothDevices, key = { device -> device.address }) { device ->
                    BluetoothDeviceItem(
                        device = device,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(AddDeviceScreenEvent.OnDeviceClicked(device.address)) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDeviceStartSearchPreview() {
    BluetoothMessagingAppTheme {
        AddDeviceScreen(
            state = AddDeviceScreenState(),
            onEvent = {},
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {
                }

                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {
                }

                override fun enableBluetooth(onResult: (Boolean) -> Unit) {
                }

                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {
                }

            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDeviceDeviceListPreview() {
    BluetoothMessagingAppTheme {
        AddDeviceScreen(
            state = AddDeviceScreenState(
                isDiscoverable = true,
                showStartSearchMessage = false,
                bluetoothDevices = listOf(
                    Connection("BluetoothDevice1", "01:23:45:67:89:AB"),
                    Connection("BluetoothDevice2", "F0:99:B6:12:34:56"),
                    Connection("BluetoothDevice3", "AA:BB:CC:DD:EE:FF")
                )
            ),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {
                }

                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {
                }

                override fun enableBluetooth(onResult: (Boolean) -> Unit) {
                }

                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {
                }

            },
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDeviceMakeDeviceVisibleErrorPreview() {
    BluetoothMessagingAppTheme {
        AddDeviceScreen(
            state = AddDeviceScreenState(
                isDiscoverable = false,
                bluetoothDevices = listOf(),
                showMakeDeviceVisibleError = true
            ),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {
                }

                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {
                }

                override fun enableBluetooth(onResult: (Boolean) -> Unit) {
                }

                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {
                }

            },
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDeviceConnectingToDeviceErrorErrorPreview() {
    BluetoothMessagingAppTheme {
        AddDeviceScreen(
            state = AddDeviceScreenState(
                isDiscoverable = false,
                showStartSearchMessage = false,
                bluetoothDevices = listOf(
                    Connection("BluetoothDevice1", "01:23:45:67:89:AB"),
                    Connection("BluetoothDevice2", "F0:99:B6:12:34:56"),
                    Connection("BluetoothDevice3", "AA:BB:CC:DD:EE:FF")
                ),
                showConnectingToDeviceError = true
            ),
            permissionHandler = object : DevicePermissionsHandler {
                override fun requestDiscoverable(onResult: (Boolean) -> Unit) {
                }

                override fun requestScanPermissions(onResult: (Boolean) -> Unit) {
                }

                override fun enableBluetooth(onResult: (Boolean) -> Unit) {
                }

                override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {
                }

            },
            onEvent = {},
        )
    }
}
