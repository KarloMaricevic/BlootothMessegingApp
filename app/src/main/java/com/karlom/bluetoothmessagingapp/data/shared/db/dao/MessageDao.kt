package com.karlom.bluetoothmessagingapp.data.shared.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(message: MessageEntity): Long

    @Insert
    suspend fun insertAll(vararg messages: MessageEntity)

    @Update
    suspend fun update(message: MessageEntity)

    @Query("SELECT * FROM MessageEntity WHERE withContactAddress = :withContactAddress ORDER BY id DESC ")
    fun loadItemDescending(withContactAddress: String): PagingSource<Int, MessageEntity>
}
