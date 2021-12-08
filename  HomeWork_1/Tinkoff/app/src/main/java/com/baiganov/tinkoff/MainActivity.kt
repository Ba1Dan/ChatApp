package com.baiganov.tinkoff

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.tinkoff.Constants.Companion.NAME_EXTRA
import com.baiganov.tinkoff.adapters.ContactsAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var rvContacts: RecyclerView
    private lateinit var btnOpen: Button
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpen= findViewById(R.id.btn_open)
        rvContacts = findViewById(R.id.rv_contacts)

        adapter = ContactsAdapter()
        rvContacts.adapter = adapter

        val secondActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val names = data.getStringArrayListExtra(NAME_EXTRA)

                    if (names != null) {
                        if (names.isEmpty()) {
                            Toast.makeText(this, "list of contacts is empty", Toast.LENGTH_SHORT).show()
                        } else {
                            adapter.setData(names)
                        }
                    } else {
                        Log.d(javaClass.simpleName, "list of names is null")
                    }
                } else {
                    Log.d(javaClass.simpleName, "Intent is null")
                }
            }
        }

        btnOpen.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            secondActivityResult.launch(intent)
        }
    }
}