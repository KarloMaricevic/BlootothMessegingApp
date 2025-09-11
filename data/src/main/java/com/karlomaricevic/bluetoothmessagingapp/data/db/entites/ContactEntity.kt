package com.karlomaricevic.bluetoothmessagingapp.data.db.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey val address: String,
    val name: String,
)