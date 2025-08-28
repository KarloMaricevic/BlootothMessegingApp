package com.karlomaricevic.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.karlomaricevic.data.db.entites.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert
    suspend fun insertAll(vararg messages: ContactEntity)

    @Query("SELECT * FROM ContactEntity ORDER BY name ASC")
    fun getAllContactsFlow(): Flow<List<ContactEntity>>
}