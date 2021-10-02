package com.baiganov.tinkoff

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.baiganov.tinkoff.Constants.Companion.ACTION_INTENT
import com.baiganov.tinkoff.Constants.Companion.NAME_EXTRA

class ContactsService : Service() {

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loadContacts()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun loadContacts() {
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val list = arrayListOf<String>()
        if (cursor != null) {

            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                list.add(name)
            }

            cursor.close()
        }

        val localIntent = Intent(ACTION_INTENT)
        localIntent.putStringArrayListExtra(NAME_EXTRA, list)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.sendBroadcast(localIntent)
    }
}