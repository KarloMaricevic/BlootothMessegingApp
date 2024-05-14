package com.karlom.bluetoothmessagingapp.data.shared.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.ContactEntity

@Dao
interface ContactDao {

    @Insert
    suspend fun insertAll(vararg messages: ContactEntity)

    @Query("SELECT * FROM ContactEntity ORDER BY name ASC")
    fun getContactPagingSource(): PagingSource<Int, ContactEntity>
}
