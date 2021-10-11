package com.baiganov.fintech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.baiganov.fintech.model.Reaction
import com.baiganov.fintech.сustomview.MessageViewGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewGroup: MessageViewGroup = findViewById(R.id.message_viewgroup)

        viewGroup.apply {
            setReactions(
                listOf(
                    Reaction("\uD83D\uDE09", 2),
                    Reaction("\uD83D\uDE09", 3),
                    Reaction("\uD83D\uDE09", 10),
                ))
            text = "Привет! Я Android разработчик"
            author = "Байганов Данияр"
        }
    }
}