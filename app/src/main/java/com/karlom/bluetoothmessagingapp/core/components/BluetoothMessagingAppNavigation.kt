package com.karlom.bluetoothmessagingapp.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.NavigateBack
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.NavigateUp
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.BluetoothDevicesScreen
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.router.BluetoothDevicesRouter
import com.karlom.bluetoothmessagingapp.feature.chat.ChatScreen
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.ChooseBluetoothTypeScreen
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.router.ChooseBluetoothTypeRouter
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
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ChooseBluetoothTypeRouter.route(),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ChooseBluetoothTypeRouter.route()) { ChooseBluetoothTypeScreen() }
            composable(BluetoothDevicesRouter.route()) { BluetoothDevicesScreen() }
            composable(ChatRouter.route()) { entry ->
                val address = entry.arguments?.getString(ChatRouter.ADDRESS_PARAM)
                    ?: error("${ChatRouter.ADDRESS_PARAM} was not provided to chat screen")
                ChatScreen(address)
            }
            composable(ContactsRouter.route()) { ContactsScreen() }
        }
    }
}
