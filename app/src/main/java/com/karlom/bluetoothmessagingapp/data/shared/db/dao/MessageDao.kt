package com.karlom.bluetoothmessagingapp.data.shared.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

@Dao
interface MessageDao {

    @Insert
    suspend fun insertAll(vararg messages: MessageEntity)
}
