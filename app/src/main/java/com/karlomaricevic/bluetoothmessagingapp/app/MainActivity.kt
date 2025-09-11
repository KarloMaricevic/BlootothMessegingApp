package com.karlomaricevic.bluetoothmessagingapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.karlomaricevic.bluetoothmessagingapp.app.di.LocalDI
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.BluetoothMessagingAppNavigation
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.app.viewmodel.GlobalViewModel
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import org.kodein.di.direct
import org.kodein.di.instance

class MainActivity : ComponentActivity() {

    private lateinit var navigator: Navigator
    private lateinit var globalViewModel: GlobalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val di = (application as BluetoothMessagingApplication).di
        navigator = di.direct.instance()
        globalViewModel = di.direct.instance<GlobalViewModel>()
        setContent {
            BluetoothMessagingAppTheme {
                CompositionLocalProvider(LocalDI provides di) {
                    BluetoothMessagingAppNavigation(navigator)
                }
            }
        }
    }
}
