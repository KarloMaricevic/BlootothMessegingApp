package com.karlomaricevic.feature.contacts

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.ContactsScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnAddContactClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnContactClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactUi
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NEW_CHAT
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NO_CONTACTS_MESSAGE
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource.Mock
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val stringResolver = mockk<StringResolver<ContactsStringKeys>>()
    private val imageResolver = mockk<ImageResolver<ContactsImageKeys>>()
    private val onEvent = mockk<(ContactScreenEvent) -> Unit>(relaxed = true)

    @Before
    fun setUp() {
        every { stringResolver.getString(any()) } returns  ""
        every { imageResolver.getImage(any()) } returns Mock
    }

    @Test
    fun shows_noContactsIndicator_whenContactsListIsEmpty() {
        val noContacts = "noContacts"
        every { stringResolver.getString(NO_CONTACTS_MESSAGE) } returns noContacts

        composeTestRule.setContent {
            ContactsScreen(
                contacts = emptyList(),
                stringResolver = stringResolver,
                imageResolver = imageResolver,
                onEvent = onEvent
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(noContacts).assertIsDisplayed()
    }

    @Test
    fun shows_listOfContacts_whenContactsAvailable() {
        val firstContact = Contact("Alice", "01:23")
        val secondContact = Contact("Bob", "45:67")
        val contacts = listOf(
            ContactUi(firstContact, Color.Red, "Hello!"),
            ContactUi(secondContact, Color.Blue, "Hi there!")
        )

        composeTestRule.setContent {
            ContactsScreen(
                contacts = contacts,
                stringResolver = stringResolver,
                imageResolver = imageResolver,
                onEvent = onEvent
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(firstContact.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(secondContact.name).assertIsDisplayed()
    }

    @Test
    fun clickingContact_sendsOnContactClickedEvent() {
        val contact = Contact("Alice", "01:23")

        composeTestRule.setContent {
            ContactsScreen(
                contacts = listOf(ContactUi(contact, Color.Red, "Hello!")),
                stringResolver = stringResolver,
                imageResolver = imageResolver,
                onEvent = onEvent
            )
        }
        composeTestRule.onNodeWithText(contact.name).performClick()

        verify { onEvent(OnContactClicked(
            contactName = contact.name,
            address = contact.address,
        )) }
    }

    @Test
    fun clickingAddContactButton_sendsOnAddContactClickedEvent() {
        val newChat = "newChat"
        every { stringResolver.getString(NEW_CHAT) } returns newChat

        composeTestRule.setContent {
            ContactsScreen(
                contacts = emptyList(),
                stringResolver = stringResolver,
                imageResolver = imageResolver,
                onEvent = onEvent
            )
        }
        composeTestRule.onNodeWithText(newChat).performClick()

        verify { onEvent(OnAddContactClicked) }
    }
}
