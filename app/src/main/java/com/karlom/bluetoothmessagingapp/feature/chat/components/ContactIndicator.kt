package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R

@Composable
fun ContactIndicator(
    name: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_avatar),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(112.dp),
        )
        Text(
            text = name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview
@Composable
private fun ContactIndicatorPreview() {
    ContactIndicator("Contact name")
}
