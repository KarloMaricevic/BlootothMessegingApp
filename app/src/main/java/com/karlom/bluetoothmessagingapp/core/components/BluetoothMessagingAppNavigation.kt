package com.karlom.bluetoothmessagingapp.core.components

import androidx.compose.foundation.layout.padding
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
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.NavigateBack
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.NavigateUp
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.feature.addDevice.AddDeviceScreen
import com.karlom.bluetoothmessagingapp.feature.addDevice.router.AddDeviceScreenRouter
import com.karlom.bluetoothmessagingapp.feature.chat.ChatScreen
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import com.karlom.bluetoothmessagingapp.feature.contacts.ContactsScreen
import com.karlom.bluetoothmessagingapp.feature.contacts.router.ContactsRouter

@Composable
fun BluetoothMessagingAppNavigation(
    navigator: Navigator,
    navController: NavHostController = rememberNavController(),
) {
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
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ContactsRouter.route(),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ChatRouter.route()) { entry ->
                val address = entry.arguments?.getString(ChatRouter.ADDRESS_PARAM)
                    ?: error("${ChatRouter.ADDRESS_PARAM} was not provided to chat screen")
                ChatScreen(address = address, scaffoldState = snackbarHostState)
            }
            composable(ContactsRouter.route()) { ContactsScreen() }
            composable(AddDeviceScreenRouter.route()) { AddDeviceScreen() }
        }
    }
}
