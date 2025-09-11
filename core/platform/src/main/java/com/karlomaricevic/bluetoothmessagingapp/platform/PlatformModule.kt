package com.karlomaricevic.bluetoothmessagingapp.platform

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val platformModule = DI.Module("PlatformModule") {

    bind<PermissionChecker>() with singleton { PermissionChecker(instance()) }

    bind<FileStorage>() with singleton { FileStorage(instance()) }
}
