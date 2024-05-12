package com.karlom.bluetoothmessagingapp.data.shared.db.enteties

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isSendByMe: Boolean,
)
