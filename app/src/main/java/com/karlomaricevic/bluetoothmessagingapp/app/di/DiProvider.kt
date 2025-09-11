package com.karlomaricevic.bluetoothmessagingapp.app.di

import androidx.compose.runtime.staticCompositionLocalOf
import org.kodein.di.DI

val LocalDI = staticCompositionLocalOf<DI> {
    error("No DI provided")
}
