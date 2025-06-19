package com.karlom.bluetoothmessagingapp.feature.contacts.mappers

import androidx.compose.ui.graphics.Color
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactUi
import javax.inject.Inject
import kotlin.math.absoluteValue

class ContactUiMapper @Inject constructor() {

    private var numberOfContactsMapped = 0
    val colors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFFF06292), // Pink
        Color(0xFFBA68C8), // Purple
        Color(0xFF64B5F6), // Blue
        Color(0xFF4DB6AC), // Teal
        Color(0xFFFFD54F), // Amber
        Color(0xFFA1887F), // Brown
    )

    fun map(contact: Contact): ContactUi {
        numberOfContactsMapped++
        return ContactUi(
            contact = contact,
            color = colors[(contact.name.hashCode().absoluteValue) % colors.size],
            lastMessage = "Last message placeholder",
        )
    }
}
