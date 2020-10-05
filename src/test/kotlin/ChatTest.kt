import org.junit.Test
import org.junit.Assert.*

class ChatTest {
    private val _chatId: Int = 33

    @Test
    fun constructor_Simple(){
        val chat = Chat(_chatId,1, 2)

        assertNotNull(chat)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructor_ExceptionWhenUserDuplicate(){
        Chat(_chatId,1, 1)
    }

    @Test
    fun createMessage_Simple(){
        val chat = Chat(_chatId,1, 2)

        var messageId = chat.createMessage(1, "asd")

        assertTrue(messageId > 0)
        assertTrue(chat.messages.count() > 0)
    }

    @Test
    fun createMessage_NotCreatedIfThirdUser(){
        val chat = Chat(_chatId,1, 2)

        var messageId = chat.createMessage(3, "asd")

        assertTrue(messageId < 0)
        assertTrue(chat.messages.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun createMessage_ExceptionWhenBlankText(){
        val chat = Chat(_chatId,1, 2)

        chat.createMessage(1, "")
    }

    @Test
    fun createMessage_CreatingMakesMessagesRead(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        chat.createMessage(2, "asd")

        assertTrue(chat.messages.first { x -> x.id == messageId }.readByAnotherUser)
    }

    @Test
    fun deleteMessage_Simple(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.deleteMessage(1, messageId)

        assertTrue(res)
        assertTrue(chat.messages.isEmpty())
    }

    @Test
    fun deleteMessage_NotDeletedIfWrongUser(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.deleteMessage(3, messageId)

        assertFalse(res)
        assertFalse(chat.messages.isEmpty())
    }

    @Test
    fun deleteMessage_DeletedIfNotExistingMessageId(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.deleteMessage(1, messageId + 1)

        assertTrue(res)
        assertFalse(chat.messages.isEmpty())
    }

    @Test
    fun updateMessage_Simple(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.updateMessage(1, messageId, "qwe")

        assertTrue(res)
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateMessage_ExceptionWhenBlankText(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        chat.updateMessage(1, messageId, "")
    }

    @Test
    fun updateMessage_NotUpdatedWhenWrongUser(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.updateMessage(3, messageId, "qwe")

        assertFalse(res)
    }

    @Test
    fun updateMessage_NotUpdateWhenNotExistingMessageId(){
        val chat = Chat(_chatId,1, 2)
        var messageId = chat.createMessage(1, "asd")

        var res = chat.updateMessage(1, messageId + 1, "qwe")

        assertFalse(res)
    }


}