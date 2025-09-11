package com.karlomaricevic.bluetoothmessagingapp.app.navigation

import androidx.navigation.NavOptionsBuilder

sealed interface NavigationEvent {
    data object NavigateUp : NavigationEvent
    data object NavigateBack : NavigationEvent
    data class Destination(
        val destination: String,
        val builder: NavOptionsBuilder.() -> Unit = {},
    ) : NavigationEvent
}
