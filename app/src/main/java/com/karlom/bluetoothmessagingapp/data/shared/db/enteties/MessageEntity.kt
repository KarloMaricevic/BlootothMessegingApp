package com.karlom.bluetoothmessagingapp.data.shared.db.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ContactEntity::class,
        parentColumns = arrayOf("address"),
        childColumns = arrayOf("withContactAddress"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isSendByMe: Boolean,
    @ColumnInfo(index = true) val withContactAddress: String
)
