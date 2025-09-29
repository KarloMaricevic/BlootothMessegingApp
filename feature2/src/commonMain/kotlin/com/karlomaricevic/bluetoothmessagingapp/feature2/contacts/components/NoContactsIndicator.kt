package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature2.Res
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts_screen_add_contacts
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts_screen_no_contacts
import com.karlomaricevic.bluetoothmessagingapp.feature2.default_icon_content_description
import com.karlomaricevic.bluetoothmessagingapp.feature2.ic_mail_box
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
            painter = painterResource(Res.drawable.ic_mail_box),
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            contentDescription = stringResource(Res.string.default_icon_content_description),
            tint = MaterialTheme.colors.onSurface,
        )
        Text(
            text = stringResource(Res.string.contacts_screen_no_contacts),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Button(
            onClick = { onInteraction(ContactScreenEvent.OnAddContactClicked) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = blue,
                contentColor = white,
            ),
        ) {
            Text(stringResource(Res.string.contacts_screen_add_contacts))
        }
    }
}
