package com.example.eventplanningapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.eventplanningapp.database.Event
import com.example.eventplanningapp.database.EventDao
import com.example.eventplanningapp.database.EventDatabase
import kotlinx.coroutines.launch

class EventViewModel(private val eventDao: EventDao) : AndroidViewModel(Application()) {

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventDao.update(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventDao.delete(event)
        }
    }
}


class EventViewModelFactory(private val eventDao: EventDao) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
