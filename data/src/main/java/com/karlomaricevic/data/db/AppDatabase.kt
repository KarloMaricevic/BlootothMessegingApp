package com.karlomaricevic.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karlomaricevic.data.db.daos.ContactDao
import com.karlomaricevic.data.db.entites.ContactEntity
import com.karlomaricevic.data.db.entites.MessageEntity
import com.karlomaricevic.data.db.daos.MessageDao

@Database(entities = [MessageEntity::class, ContactEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun contactDao(): ContactDao
}
