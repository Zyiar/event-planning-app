package com.example.eventplanningapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    val name: String,
    val date: String,
    val location: String,
    val theme: String,
    val timeline: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
