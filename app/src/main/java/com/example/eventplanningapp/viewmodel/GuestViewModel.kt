package com.example.eventplanningapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.eventplanningapp.database.GuestDatabase
import com.example.eventplanningapp.database.Guest
import com.example.eventplanningapp.database.GuestDao

class GuestViewModel(application: Application) : AndroidViewModel(application) {
    private val guestDao: GuestDao = GuestDatabase.getDatabase(application).guestDao()

    private val _guests = MutableLiveData<List<Guest>>()
    val guests: LiveData<List<Guest>> get() = _guests

    init {
        // Initialize the guests list with existing guests from the database
        viewModelScope.launch {
            _guests.value = guestDao.getAllGuests()
        }
    }

    fun addGuest(guest: Guest) {
        viewModelScope.launch {
            guestDao.insert(guest) // Insert guest into database
            Log.d("GuestViewModel", "Added guest: ${guest.name}")
            // Emit updated list of guests to LiveData
            _guests.value = guestDao.getAllGuests()
        }
    }

    fun updateGuestInvitationStatus(guestId: Long, status: Boolean) {
        viewModelScope.launch {
            val guest = guestDao.getAllGuests().first { it.id == guestId }
            guestDao.update(guest.copy(isInvited = status))
        }
    }

    fun removeGuest(guestId: Long) {
        viewModelScope.launch {
            val guest = guestDao.getAllGuests().firstOrNull { it.id == guestId }
            guest?.let {
                guestDao.delete(it)  // Delete the guest
                // After deletion, refresh the guests list
                _guests.value = guestDao.getAllGuests()
            }
        }
    }

    fun updateRSVPStatus(guestId: Long, status: String) {
        viewModelScope.launch {
            val guest = guestDao.getAllGuests().first { it.id == guestId }
            guestDao.update(guest.copy(rsvpStatus = status))
            _guests.value = guestDao.getAllGuests() // Refresh the guest list
        }
    }

    fun getRSVPSummary(): Map<String, Int> {
        val guestsList = _guests.value ?: emptyList()
        return mapOf(
            "Attending" to guestsList.count { it.rsvpStatus == "Attending" },
            "Not Attending" to guestsList.count { it.rsvpStatus == "Not Attending" },
            "No Response" to guestsList.count { it.rsvpStatus == null }
        )
    }


}


