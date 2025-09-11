package com.karlomaricevic.bluetoothmessagingapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.ContactDao
import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.MessageDao
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.ContactEntity
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageEntity

@Database(entities = [MessageEntity::class, ContactEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun contactDao(): ContactDao
}
