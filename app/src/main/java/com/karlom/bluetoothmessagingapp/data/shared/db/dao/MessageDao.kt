package com.karlom.bluetoothmessagingapp.data.shared.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

@Dao
interface MessageDao {

    @Insert
    suspend fun insertAll(vararg messages: MessageEntity)

    @Query("SELECT * FROM MessageEntity ORDER BY id ASC")
    fun getMessages(): PagingSource<Int, MessageEntity>
}
