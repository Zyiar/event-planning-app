package com.example.eventplanningapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
data class Guest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val isInvited: Boolean = false,
    val rsvpStatus: String? = null,
)
