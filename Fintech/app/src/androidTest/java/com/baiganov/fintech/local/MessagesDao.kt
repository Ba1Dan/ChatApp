package com.baiganov.fintech.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.baiganov.fintech.data.db.ChatDatabase
import com.baiganov.fintech.data.db.dao.MessagesDao
import com.baiganov.fintech.data.db.entity.MessageEntity
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessagesDao {

    private lateinit var database: ChatDatabase
    private lateinit var messagesDao: MessagesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ChatDatabase::class.java
        ).allowMainThreadQueries().build()
        messagesDao = database.messagesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDb() {
        val message1 = MessageEntity(
            id = 0,
            avatarUrl = "hhfdhgf",
            content = "fghgfh",
            reactions = mutableListOf(),
            senderEmail = "fghgfh",
            senderFullName = "fghfgh",
            senderId = 0,
            timestamp = 0L,
            streamId = 0,
            topicName = "fghfgh"
        )

        messagesDao.saveMessages(listOf(message1)).test()
        var messages = messagesDao.getStreamMessages(0).blockingFirst()
        Assert.assertEquals(messages[0].content, message1.content)

        val message2 = MessageEntity(
            id = 1,
            avatarUrl = "hhfdhgf",
            content = "fghgfh",
            reactions = mutableListOf(),
            senderEmail = "fghgfh",
            senderFullName = "fghfgh",
            senderId = 0,
            timestamp = 0L,
            streamId = 0,
            topicName = "fghfgh"
        )

        messagesDao.saveMessages(listOf(message2)).test()
        messages = messagesDao.getStreamMessages(0).blockingFirst()
        Assert.assertEquals(messages.size,2)

        Assert.assertEquals(messages[0].topicName, message1.topicName)
        Assert.assertEquals(messages[1].topicName, message2.topicName)
    }

    @Test
    fun deleteDb() {
        val item = MessageEntity(
            id = 0,
            avatarUrl = "hhfdhgf",
            content = "fghgfh",
            reactions = mutableListOf(),
            senderEmail = "fghgfh",
            senderFullName = "fghfgh",
            senderId = 0,
            timestamp = 0L,
            streamId = 0,
            topicName = "fghfgh"
        )

        messagesDao.saveMessages(listOf(item)).test()
        messagesDao.deleteStreamMessages(0).test()

        messagesDao.getStreamMessages(0).test().assertValue { list -> list.isEmpty() }
    }
}