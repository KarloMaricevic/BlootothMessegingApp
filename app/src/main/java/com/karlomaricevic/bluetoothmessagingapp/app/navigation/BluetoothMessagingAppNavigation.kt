package com.karlomaricevic.bluetoothmessagingapp.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karlomaricevic.bluetoothmessagingapp.app.di.LocalDI
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.NavigationEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.AddDeviceScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceScreenRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.ChatScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatRouter.CONTACT_NAME_PARAM
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.ContactsScreen
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel.ContactsViewModel
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
                val contactName = entry.arguments?.getString(ChatRouter.CONTACT_NAME_PARAM)
                    ?: error("Contact name not provided")
                ChatScreen(
                    viewModel = viewModel,
                    contactName = contactName,
                    scaffoldState = snackbarHostState,
                )
            }
            composable(ContactsRouter.route()) {
                val viewmodel by di.instance<ContactsViewModel>()
                ContactsScreen(viewmodel)
            }
            composable(AddDeviceScreenRouter.route()) {
                val viewModel by di.instance<AddDeviceViewModel>()
                AddDeviceScreen(viewModel)
            }
        }
    }
}
