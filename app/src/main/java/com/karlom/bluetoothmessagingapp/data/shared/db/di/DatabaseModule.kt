package com.karlom.bluetoothmessagingapp.data.shared.db.di

import android.content.Context
import androidx.room.Room
import com.karlom.bluetoothmessagingapp.data.shared.db.AppDatabase
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    companion object {
        @Singleton
        @Provides
        fun bindDatabase(@ApplicationContext appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "BluetoothMessagingAppDb"
            ).build()
        }

        @Singleton
        @Provides
        fun provideMessageDao(database: AppDatabase): MessageDao {
            return database.messageDao()
        }
    }
}
