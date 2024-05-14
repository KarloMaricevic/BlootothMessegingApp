package com.karlom.bluetoothmessagingapp.data.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.ContactDao
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.ContactEntity
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

@Database(entities = [MessageEntity::class, ContactEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun contactDao(): ContactDao
}
