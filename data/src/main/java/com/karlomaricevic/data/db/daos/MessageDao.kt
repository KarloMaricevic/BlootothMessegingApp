package com.karlomaricevic.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.karlomaricevic.data.db.entites.MessageEntity
import com.karlomaricevic.domain.messaging.models.SendMessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(message: MessageEntity): Long

    @Insert
    suspend fun insertAll(vararg messages: MessageEntity)

    @Query("UPDATE MessageEntity SET state = :newState WHERE id = :id")
    suspend fun updateStateById(id: Long, newState: SendMessageStatus)

    @Update
    suspend fun update(message: MessageEntity)

    @Query("SELECT * FROM MessageEntity WHERE withContactAddress = :withContactAddress ORDER BY id DESC")
    fun loadAllMessagesForUser(withContactAddress: String): Flow<List<MessageEntity>>
}