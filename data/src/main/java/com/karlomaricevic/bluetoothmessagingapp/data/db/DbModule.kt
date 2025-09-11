package com.karlomaricevic.bluetoothmessagingapp.data.db

import android.content.Context
import androidx.room.Room
import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.ContactDao
import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.MessageDao
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val dbModule = DI.Module("DbDataModule") {

    // Provide AppDatabase singleton
    bind<AppDatabase>() with singleton {
        Room.databaseBuilder(
            context = instance<Context>().applicationContext,
            klass = AppDatabase::class.java,
            name = "app_database"
        ).build()
    }

    bind<ContactDao>() with singleton { instance<AppDatabase>().contactDao() }
    bind<MessageDao>() with singleton { instance<AppDatabase>().messageDao() }
}
