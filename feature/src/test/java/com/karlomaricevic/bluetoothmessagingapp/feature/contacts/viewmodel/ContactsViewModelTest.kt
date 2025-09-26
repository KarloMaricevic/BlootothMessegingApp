package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnAddContactClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactUi
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val getContacts: GetContacts = mockk()
    private val contactMapper: ContactUiMapper = mockk()
    private val navigator: ContactsNavigator = mockk()

    private lateinit var sut: ContactsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getContacts_MapsDomainContactsToUiContacts() = testScope.runTest {
        val firstContact = Contact(name = "Alice", address = "")
        val secondContact = Contact(name = "Bob", address = "")
        val contacts = listOf(firstContact, secondContact)
        val uiContacts = listOf(
            ContactUi(contact = firstContact, color = Color.Red, lastMessage = ""),
            ContactUi(contact = secondContact, color = Color.Blue, lastMessage = ""),
        )
        val contactsFlow = flowOf(contacts)
        coEvery { getContacts() } returns contactsFlow
        every { contactMapper.map(contacts[0]) } returns uiContacts[0]
        every { contactMapper.map(contacts[1]) } returns uiContacts[1]
        sut = ContactsViewModel(
            getContacts = getContacts,
            contactMapper = contactMapper,
            navigator = navigator,
            vmScope = testScope.backgroundScope,
        )

        sut.contacts.test {
            awaitItem() shouldBe null
            awaitItem() shouldBe uiContacts
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onEvent_OnAddContactClicked_NavigatesToAddDeviceScreen() = testScope.runTest {
        coEvery { getContacts() } returns MutableStateFlow(emptyList())
        sut = ContactsViewModel(getContacts, contactMapper, navigator, testScope)
        coEvery { navigator.navigateToAddDeviceScreen() } just Runs

        sut.onEvent(OnAddContactClicked)
        testScheduler.advanceUntilIdle()

        coVerify { navigator.navigateToAddDeviceScreen() }
    }

    @Test
    fun onEvent_OnContactClicked_NavigatesToChatScreen() = testScope.runTest {
        // given
        coEvery { getContacts() } returns MutableStateFlow(emptyList())
        sut = ContactsViewModel(getContacts, contactMapper, navigator, testScope)
        coEvery { navigator.navigateToChatScreen("Alice", "00:11:22:33") } just Runs

        // when
        sut.onEvent(ContactScreenEvent.OnContactClicked("Alice", "00:11:22:33"))
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        coVerify { navigator.navigateToChatScreen("Alice", "00:11:22:33") }
    }

    // Fake classes for test purposes
    data class FakeDomainContact(val name: String)
    data class FakeUiContact(val name: String)
}
