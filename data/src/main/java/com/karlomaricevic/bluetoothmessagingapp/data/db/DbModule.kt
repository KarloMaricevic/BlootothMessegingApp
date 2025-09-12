package com.karlomaricevic.bluetoothmessagingapp.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val dbModule = DI.Module("DbDataModule") {

    bind<Database>() with singleton {
        Database(
            driver = AndroidSqliteDriver(
                schema = Database.Schema,
                context = instance<Context>().applicationContext,
                name = "app_database.db"
            )
        )
    }
    bind<ContactQueries>() with singleton { instance<Database>().contactQueries }
    bind<MessageQueries>() with singleton { instance<Database>().messageQueries }

}
