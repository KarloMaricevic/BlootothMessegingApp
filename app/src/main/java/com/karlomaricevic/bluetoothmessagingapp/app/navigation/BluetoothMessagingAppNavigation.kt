package com.karlomaricevic.bluetoothmessagingapp.app.navigation

import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.AddDeviceScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karlomaricevic.bluetoothmessagingapp.app.BluetoothMessagingApplication
import com.karlomaricevic.bluetoothmessagingapp.app.di.LocalDI
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.NavigationEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceScreenRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AndroidAddDeviceViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.ChatScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel.AndroidChatViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsRouter
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.ContactsScreen
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.viewmodel.ContactsViewModel
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DIContainer
import org.kodein.di.direct
import org.kodein.di.factory
import org.kodein.di.instance

@Composable
fun BluetoothMessagingAppNavigation(
    navigator: Navigator,
    navController: NavHostController = rememberNavController(),
) {
    val di = LocalDI.current
    LaunchedEffect(key1 = Unit) {
        navigator.navigationEvent.collect { navigationEvent ->
            when (navigationEvent) {
                NavigateUp -> navController.navigateUp()
                NavigateBack -> navController.popBackStack()
                is Destination -> navController.navigate(
                    route = navigationEvent.destination,
                    builder = navigationEvent.builder,
                )
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ContactsRouter.route(),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ChatRouter.route()) { entry ->
                val viewModel by di.instance<ChatViewModel>()
                val factory: (SavedStateHandle, CoroutineScope) -> ChatViewModel =
                    { handle, scope ->
                        di.direct.factory<Pair<SavedStateHandle, CoroutineScope>, ChatViewModel>()
                            .invoke(handle to scope)
                    }
                val androidVM: AndroidChatViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                            val savedStateHandle = extras.createSavedStateHandle()
                            return AndroidChatViewModel { scope ->
                                factory(savedStateHandle, scope)
                            } as T
                        }
                    }
                )
                val contactName = entry.arguments?.getString(ChatRouter.CONTACT_NAME_PARAM)
                    ?: error("Contact name not provided")
                ChatScreen(
                    state = androidVM.state.collectAsState().value,
                    contactName = contactName,
                    onEvent = androidVM::onEvent,
                    scaffoldState = snackbarHostState,
                    effectFlow = viewModel.viewEffect,
                )
            }
            composable(ContactsRouter.route()) {
                val viewModel = di.direct.instance<ContactsViewModel>()
                ContactsScreen(
                    contacts = viewModel.contacts.collectAsState().value,
                    onEvent = viewModel::onEvent,
                )
            }
            composable(AddDeviceScreenRouter.route()) {
                val factory: (CoroutineScope) -> AddDeviceViewModel =
                    di.direct.factory<CoroutineScope, AddDeviceViewModel>()
                val androidVM: AndroidAddDeviceViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AndroidAddDeviceViewModel(factory) as T
                        }
                    }
                )
                AddDeviceScreen(
                    state = androidVM.state.collectAsState().value,
                    onEvent = androidVM::onEvent,
                )
            }
        }
    }
}
