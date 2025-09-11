package com.karlomaricevic.bluetoothmessagingapp.app.di.data

import android.content.Context
import androidx.room.Room
import com.karlomaricevic.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DbModule {

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase {
            return Room.databaseBuilder(
                context = context.applicationContext,
                klass = AppDatabase::class.java,
                name = "app_database",
            ).build()
        }

        @Provides
        @Singleton
        fun providesContactsDao(appDatabase: AppDatabase) = appDatabase.contactDao()

        @Provides
        @Singleton
        fun providesMessageDao(appDatabase: AppDatabase) = appDatabase.messageDao()
    }
}
