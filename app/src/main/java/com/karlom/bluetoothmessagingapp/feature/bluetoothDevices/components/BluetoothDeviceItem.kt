package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(start = 8.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_bluetooth),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier.padding(end = 8.dp),
        )
        Column {
            Text(
                text = device.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 2.dp),
            )
            Text(text = device.address)
        }
    }
}

@Preview
@Composable
fun BluetoothDeviceItemPreview() {
    BluetoothDeviceItem(
        BluetoothDevice(
            name = "Device",
            address = "FA:92:46:11:02:43",
        )
    )
}

@Preview
@Composable
fun BluetoothDeviceItemLongTextPreview() {
    BluetoothDeviceItem(
        BluetoothDevice(
            name = List(10) { "Device" }.joinToString(),
            address = "FA:92:46:11:02:43",
        )
    )
}
