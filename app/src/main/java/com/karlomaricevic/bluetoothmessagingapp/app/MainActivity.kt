package com.karlomaricevic.bluetoothmessagingapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.BluetoothMessagingAppNavigation
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.app.viewmodel.GlobalViewModel
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var globalViewModel: GlobalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        globalViewModel = ViewModelProvider(this)[GlobalViewModel::class.java]
        setContent {
            BluetoothMessagingAppTheme {
                BluetoothMessagingAppNavigation(navigator)
            }
        }
    }
}
