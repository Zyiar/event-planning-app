package com.example.eventplanningapp.fragments

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventplanningapp.database.Event
import com.example.eventplanningapp.database.EventDatabase
import com.example.eventplanningapp.viewmodel.EventViewModel
import com.example.eventplanningapp.viewmodel.EventViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventFragment(navController: NavController, modifier: Modifier = Modifier) {
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(EventDatabase.getDatabase(navController.context).eventDao())
    )

    val events by eventViewModel.allEvents.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<Event?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Event Planning", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Create Event")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (events.isEmpty()) {
            Text(
                "No events added yet!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(events) { event ->
                    EventItem(
                        event = event,
                        onEdit = { eventToEdit = it; showDialog = true },
                        onDelete = { eventViewModel.deleteEvent(it) }
                    )
                }
            }
        }

        if (showDialog) {
            AddEventDialog(
                eventToEdit = eventToEdit,
                onDismiss = {
                    showDialog = false
                    eventToEdit = null
                },
                onAddEvent = { name, date, location, theme, timeline ->
                    if (eventToEdit == null) {
                        eventViewModel.addEvent(Event(name, date, location, theme, timeline))
                    } else {
                        eventViewModel.updateEvent(
                            eventToEdit!!.copy(name = name, date = date, location = location, theme = theme, timeline = timeline)
                        )
                    }
                    showDialog = false
                    eventToEdit = null
                }
            )
        }
    }
}

@Composable
fun AddEventDialog(
    eventToEdit: Event? = null,
    onDismiss: () -> Unit,
    onAddEvent: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(eventToEdit?.name ?: "") }
    var date by remember { mutableStateOf(eventToEdit?.date ?: "") }
    var location by remember { mutableStateOf(eventToEdit?.location ?: "") }
    var theme by remember { mutableStateOf(eventToEdit?.theme ?: "") }
    var timeline by remember { mutableStateOf(eventToEdit?.timeline ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context = LocalContext.current,
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                date = selectedDate
                showDatePicker = false
            }
        )
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (eventToEdit == null) "Add New Event" else "Edit Event") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Event Name") })
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Event Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Pick Date")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = location, onValueChange = { location = it }, label = { Text("Event Location") })
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = theme, onValueChange = { theme = it }, label = { Text("Event Theme") })
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = timeline, onValueChange = { timeline = it }, label = { Text("Event Timeline") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && date.isNotEmpty()) {
                        onAddEvent(name, date, location, theme, timeline)
                    }
                }
            ) {
                Text(if (eventToEdit == null) "Add Event" else "Save Changes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EventItem(event: Event, onEdit: (Event) -> Unit, onDelete: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(event.name, style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(onClick = { onEdit(event) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Event",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onDelete(event) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Event",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Date: ${event.date}", style = MaterialTheme.typography.bodyLarge)
            Text("Location: ${event.location}", style = MaterialTheme.typography.bodyLarge)
            Text("Theme: ${event.theme}", style = MaterialTheme.typography.bodyLarge)
            Text("Timeline: ${event.timeline}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DatePickerDialog(context: android.content.Context, onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    androidx.compose.runtime.DisposableEffect(Unit) {
        val datePicker = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
        onDispose { datePicker.dismiss() }
    }
}

