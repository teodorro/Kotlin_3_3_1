import org.junit.Test
import org.junit.Assert.*

class ChatServiceTest {

    // region createMessage
    @Test
    fun createMessage_OkIfNoChatExists(){
        var message = ChatService.createMessage(123, 234, "asd")

        assertNotNull(message)
    }

    @Test
    fun createMessage_OkIfChatExists(){
        ChatService.createMessage(123, 234, "asd")
        var message2 = ChatService.createMessage(123, 234, "ewq")

        assertNotNull(message2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createMessage_ExceptionIfDuplicateUserIds(){
        ChatService.createMessage(123, 123, "asd")
    }

    @Test (expected = IllegalArgumentException::class)
    fun createMessage_ExceptionIfBlankText(){
        ChatService.createMessage(123, 234, " ")
    }

    @Test
    fun createMessage_UserIdsOrderDoesntMatter(){
        ChatService.createMessage(123, 234, "asd")
        ChatService.createMessage(234, 123, "ewq")

        var chats = ChatService.getChats(123)

        assertEquals(1, chats.size)
    }

    // endregion createMessage

    // region getChats
    @Test
    fun getChats_ChatsExist(){
        ChatService.createMessage(123, 234, "asd")

        var chats = ChatService.getChats(123)

        assertEquals(1, chats.size)
    }

    @Test
    fun getChats_ChatsNotExist(){
        var chats = ChatService.getChats(1234)

        assertEquals(0, chats.size)
    }
    // endregion getChats

    // region getMessages
    @Test
    fun getMessages_Simple(){
        ChatService.createMessage(123, 234, "asd")
        var messageId = ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")
        ChatService.createMessage(123, 234, "ert")

        var messages = ChatService.getMessages(123, ChatService.getChatId(123, 234) as Int, messageId, 2)

        assertEquals(2, messages.size)
    }

    @Test
    fun getMessages_UserNotExists(){
        ChatService.createMessage(123, 234, "asd")
        var messageId = ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")

        var messages = ChatService.getMessages(666, ChatService.getChatId(123, 234) as Int, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_ChatNotExists(){
        ChatService.createMessage(123, 234, "asd")
        var messageId = ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")

        var messages = ChatService.getMessages(123, 666, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_MessageStartFromNotExists(){
        ChatService.createMessage(123, 234, "asd")
        ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")

        var messages = ChatService.getMessages(123, ChatService.getChatId(123, 234) as Int, 666, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_AmountTooLarge(){
        ChatService.createMessage(123, 234, "asd")
        var messageId = ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")

        var messages = ChatService.getMessages(123, ChatService.getChatId(123, 234) as Int, messageId, 100500)

        assertEquals(2, messages.size)
    }

    @Test
    fun getMessages_AmountTooSmall(){
        ChatService.createMessage(123, 234, "asd")
        var messageId = ChatService.createMessage(123, 234, "qwe")
        ChatService.createMessage(123, 234, "zxc")

        var messages = ChatService.getMessages(123, 666, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_InvokingMakesMessagesRead(){
        var messageId = ChatService.createMessage(123, 234, "asd")
        ChatService.createMessage(123, 234, "qwe")
        val chatId = ChatService.getChatId(123, 234) as Int

        var messagesUser1 = ChatService.getMessages(123, chatId, messageId, 2)
        messagesUser1.forEach { x -> assertFalse(x.readByAnotherUser) }

        var messagesUser2 = ChatService.getMessages(234, chatId, messageId, 2)
        messagesUser2.forEach { x -> assertTrue(x.readByAnotherUser) }
    }

    // endregion getMessages

    // region deleteChat

    @Test
    fun deleteChat_Simple(){
        ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteChat(123, ChatService.getChatId(123, 234) as Int)

        assertTrue(res)
    }

    @Test
    fun deleteChat_UserNotExists(){
        ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteChat(666, ChatService.getChatId(123, 234) as Int)

        assertFalse(res)
    }

    @Test
    fun deleteChat_ChatNotExists(){
        ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteChat(123, 666)

        assertFalse(res)
    }

    // endregion deleteChat

    // region deleteMessage

    @Test
    fun deleteMessage_Simple(){
        var messageId = ChatService.createMessage(125, 236, "asd")
        ChatService.createMessage(125, 236, "qwe")

        var res = ChatService.deleteMessage(125, ChatService.getChatId(125, 236) as Int, messageId)

        assertTrue(res)
    }

    @Test
    fun deleteMessage_UserNotExists(){
        var messageId = ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteMessage(666, ChatService.getChatId(123, 234) as Int, messageId)

        assertFalse(res)
    }

    @Test
    fun deleteMessage_ChatNotExists(){
        var messageId = ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteMessage(123, 666, messageId)

        assertFalse(res)
    }

    @Test
    fun deleteMessage_MessageNotExists(){
        var messageId = ChatService.createMessage(123, 234, "asd")

        var res = ChatService.deleteMessage(123, ChatService.getChatId(123, 234) as Int, messageId + 1)

        assertTrue(res)
    }

    @Test
    fun deleteMessage_EmptyChatWasDeleted(){
        var messageId = ChatService.createMessage(123, 234, "asd")

        ChatService.deleteMessage(123, ChatService.getChatId(123, 234) as Int, messageId)
        var chatExists = ChatService.getChats(123).any { x -> x.id == ChatService.getChatId(123, 234) as Int }

        assertFalse(chatExists)
    }

    // endregion deleteMessage


    // region getUnreadChats

    @Test
    fun getUnreadChats_UnreadMessagesExist(){
        ChatService.createMessage(123, 234, "asd")
        ChatService.getMessages(234, ChatService.getChatId(123, 234) as Int, 0, 1)
        ChatService.createMessage(567, 234, "xzc")
        ChatService.createMessage(123, 234, "qwe")

        var chats = ChatService.getUnreadChats(234)

        assertEquals(2, chats.size)
    }

    @Test
    fun getUnreadChats_AllMessagesRead(){
        var ind1 = ChatService.createMessage(123, 234, "asd")
        var ind2 = ChatService.createMessage(567, 234, "xzc")
        ChatService.getMessages(234, ChatService.getChatId(123, 234) as Int, ind1, 1)
        ChatService.getMessages(234, ChatService.getChatId(567, 234) as Int, ind2, 1)

        var chats = ChatService.getUnreadChats(234)

        assertEquals(0, chats.size)
    }

    @Test
    fun getUnreadChats_UserNotExists(){
        ChatService.createMessage(123, 234, "asd")

        var chats = ChatService.getUnreadChats(666)

        assertEquals(0, chats.size)
    }

    @Test
    fun getUnreadChats_NoChats(){
        var chats = ChatService.getUnreadChats(2345)

        assertEquals(0, chats.size)
    }

    // endregion getUnreadChats


    // region getUnreadChatsCount

    @Test
    fun getUnreadChatsCount_ChatsExist(){
        ChatService.createMessage(123, 234, "asd")
        ChatService.getMessages(234, ChatService.getChatId(123, 234) as Int, 0, 1)
        ChatService.createMessage(567, 234, "xzc")
        ChatService.createMessage(123, 234, "qwe")

        var num = ChatService.getUnreadChatsCount(234)

        assertEquals(2, num)
    }


    @Test
    fun getUnreadChatsCount_NoChats(){
        var chats = ChatService.getUnreadChats(666)

        assertEquals(0, chats.size)
    }

    // endregion getUnreadChatsCount
}