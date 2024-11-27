package com.example.eventplanningapp.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventplanningapp.database.Guest
import com.example.eventplanningapp.viewmodel.GuestViewModel
import com.example.eventplanningapp.viewmodel.GuestViewModelFactory
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.livedata.observeAsState
import com.example.eventplanningapp.utils.sendSMS

@Composable
fun GuestsFragment(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val guestViewModel: GuestViewModel = viewModel(factory = GuestViewModelFactory(context))

    // Observe the guest list from the ViewModel
    val guests: List<Guest> by guestViewModel.guests.observeAsState(emptyList())

    var selectedContacts by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // Check and request permissions for reading contacts and sending SMS
    val readContactsPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_CONTACTS
    )

    val sendSmsPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.SEND_SMS
    )

    if (readContactsPermission != PackageManager.PERMISSION_GRANTED || sendSmsPermission != PackageManager.PERMISSION_GRANTED) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isContactsPermissionGranted = permissions[android.Manifest.permission.READ_CONTACTS] ?: false
            val isSmsPermissionGranted = permissions[android.Manifest.permission.SEND_SMS] ?: false

            if (isContactsPermissionGranted && isSmsPermissionGranted) {
                Toast.makeText(context, "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.SEND_SMS
                )
            )
        }
    }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { contactUri ->
                try {
                    val contactDetails = getContactDetails(context, contactUri)
                    contactDetails?.let { (contactName, contactPhoneNumber) ->
                        if (contactName != null && contactPhoneNumber != null) {
                            val guest = Guest(name = contactName, phoneNumber = contactPhoneNumber)
                            guestViewModel.addGuest(guest)
                        } else {
                            Log.e("GuestsFragment", "Contact details not found")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GuestsFragment", "Error retrieving contact details", e)
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                contactPickerLauncher.launch(pickContactIntent)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
        ) {
            Text("Pick Contact")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Display the guest list
        if (guests.isEmpty()) {
            Text("No guests added yet !")
        } else {
            Column {
                guests.forEach { guest ->
                    GuestRow(
                        guest = guest,
                        isSelected = selectedContacts.contains(guest.id),
                        onSelectChange = { isChecked ->
                            selectedContacts = if (isChecked) {
                                selectedContacts + guest.id
                            } else {
                                selectedContacts - guest.id
                            }
                        },
                        onRSVPChange = { status ->
                            guestViewModel.updateRSVPStatus(guest.id, status)
                        },
                        onDelete = {
                            guestViewModel.removeGuest(guest.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Button to send invites
        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            onClick = {
            val phoneNumbers = guests.filter { guest ->
                selectedContacts.contains(guest.id)
            }.map { guest ->
                guest.phoneNumber
            }

            if (phoneNumbers.isNotEmpty()) {
                val message = "You're invited to our event! RSVP now."
                sendSMS(context, message, phoneNumbers)
            }
        }) {
            Text("Send Invites")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Display RSVP summary
        val rsvpSummary = guestViewModel.getRSVPSummary()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "RSVP Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Attending: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${rsvpSummary["Attending"] ?: 0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Not Attending: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${rsvpSummary["Not Attending"] ?: 0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No Response: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${rsvpSummary["No Response"] ?: 0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun GuestRow(
    guest: Guest,
    isSelected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    onRSVPChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRSVP by remember { mutableStateOf(guest.rsvpStatus ?: "No Response") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(guest.name, modifier = Modifier.weight(1f))

        // New Dropdown for RSVP status
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        selectedRSVP = "Attending"
                        onRSVPChange("Attending")
                        expanded = false
                    },
                    text = { Text("Attending") }
                )

                DropdownMenuItem(
                    onClick = {
                        selectedRSVP = "Not Attending"
                        onRSVPChange("Not Attending")
                        expanded = false
                    },
                    text = { Text("Not Attending") }
                )

                DropdownMenuItem(
                    onClick = {
                        selectedRSVP = "No Response"
                        onRSVPChange("No Response")
                        expanded = false
                    },
                    text = { Text("No Response") }
                )
            }

            TextButton(onClick = { expanded = !expanded }) {
                Text(text = selectedRSVP)
            }
        }

        // Checkbox for selection
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectChange
        )

        // Delete button
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Guest")
        }
    }
}

private fun getContactDetails(context: Context, contactUri: Uri): Pair<String?, String?>? {
    return try {
        // Query the ContactsContract.CommonDataKinds.Phone content provider for phone number and name
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?", // Ensure you filter by the correct contact
            arrayOf(contactUri.lastPathSegment), // Contact ID is passed here
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                val phoneNumberIndex = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val contactName = it.getString(nameIndex)
                val contactPhoneNumber = it.getString(phoneNumberIndex)

                Pair(contactName, contactPhoneNumber)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        Log.e("GuestsFragment", "Error querying contact details", e)
        null
    }
}
