package com.example.eventplanningapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GuestDao {
    @Insert
    suspend fun insert(guest: Guest)

    @Update
    suspend fun update(guest: Guest)

    @Delete
    suspend fun delete(guest: Guest)

    @Query("SELECT * FROM guests")
    suspend fun getAllGuests(): List<Guest>

    @Query("SELECT * FROM guests WHERE id = :id")
    suspend fun getGuestById(id: Long): Guest?
}

