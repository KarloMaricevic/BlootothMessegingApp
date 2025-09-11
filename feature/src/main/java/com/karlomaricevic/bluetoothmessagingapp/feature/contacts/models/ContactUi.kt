package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models

import androidx.compose.ui.graphics.Color
import com.karlomaricevic.domain.contacts.models.Contact

data class ContactUi(
    val contact: Contact,
    val color: Color,
    val lastMessage: String,
)
