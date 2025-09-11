package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent

@Composable
fun NoContactsIndicator(
    onInteraction: (ContactScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_mail_box),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.contacts_screen_no_contacts),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Button(
            onClick = { onInteraction(ContactScreenEvent.OnAddContactClicked) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue,
                contentColor = white,
            ),
        ) {
            Text(stringResource(R.string.contacts_screen_add_contacts))
        }
    }
}

@Preview
@Composable
private fun NoContactsIndicatorPreview() {
    BluetoothMessagingAppTheme {
        NoContactsIndicator({})
    }
}
