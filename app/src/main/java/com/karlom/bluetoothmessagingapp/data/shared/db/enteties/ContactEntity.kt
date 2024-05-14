package com.karlom.bluetoothmessagingapp.data.shared.db.enteties

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey val address: String,
    val name: String,
)
