package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models

import androidx.compose.ui.graphics.Color
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact

data class ContactUi(
    val contact: Contact,
    val color: Color,
    val lastMessage: String,
)
