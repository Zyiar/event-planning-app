package com.example.eventplanningapp.utils

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

fun sendSMS(context: Context, message: String, phoneNumbers: List<String>) {
    val smsManager: SmsManager = SmsManager.getDefault()

    // Iterate through each phone number and send the SMS
    for (phoneNumber in phoneNumbers) {
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "Message/s sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send message to $phoneNumber", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
