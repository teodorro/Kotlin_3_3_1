import org.junit.Test
import org.junit.Assert.*

class ChatServiceTest {

    // region createMessage
    @Test
    fun createMessage_OkIfNoChatExists(){
        var message = ChatService().createMessage(123, 234, "asd")

        assertNotNull(message)
    }

    @Test
    fun createMessage_OkIfChatExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var message2 = chatService.createMessage(123, 234, "ewq")

        assertNotNull(message2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createMessage_ExceptionIfDuplicateUserIds(){
        ChatService().createMessage(123, 123, "asd")
    }

    @Test (expected = IllegalArgumentException::class)
    fun createMessage_ExceptionIfBlankText(){
        ChatService().createMessage(123, 234, " ")
    }

    @Test
    fun createMessage_UserIdsOrderDoesntMatter(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        chatService.createMessage(234, 123, "ewq")

        var chats = chatService.getChats(123)

        assertEquals(1, chats.size)
    }

    // endregion createMessage

    // region getChats
    @Test
    fun getChats_ChatsExist(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")

        var chats = chatService.getChats(123)

        assertEquals(1, chats.size)
    }

    @Test
    fun getChats_ChatsNotExist(){
        var chats = ChatService().getChats(1234)

        assertEquals(0, chats.size)
    }
    // endregion getChats

    // region getMessages
    @Test
    fun getMessages_Simple(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var messageId = chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")
        chatService.createMessage(123, 234, "ert")

        var messages = chatService.getMessages(123, chatService.getChatId(123, 234) as Int, messageId, 2)

        assertEquals(2, messages.size)
    }

    @Test
    fun getMessages_UserNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var messageId = chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")

        var messages = chatService.getMessages(666, chatService.getChatId(123, 234) as Int, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_ChatNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var messageId = chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")

        var messages = chatService.getMessages(123, 666, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_MessageStartFromNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")

        var messages = chatService.getMessages(123, chatService.getChatId(123, 234) as Int, 666, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_AmountTooLarge(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var messageId = chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")

        var messages = chatService.getMessages(123, chatService.getChatId(123, 234) as Int, messageId, 100500)

        assertEquals(2, messages.size)
    }

    @Test
    fun getMessages_AmountTooSmall(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        var messageId = chatService.createMessage(123, 234, "qwe")
        chatService.createMessage(123, 234, "zxc")

        var messages = chatService.getMessages(123, 666, messageId, 2)

        assertEquals(0, messages.size)
    }

    @Test
    fun getMessages_InvokingMakesMessagesRead(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(123, 234, "asd")
        chatService.createMessage(123, 234, "qwe")
        val chatId = chatService.getChatId(123, 234) as Int

        var messagesUser1 = chatService.getMessages(123, chatId, messageId, 2)
        messagesUser1.forEach { x -> assertFalse(x.readByAnotherUser) }

        var messagesUser2 = chatService.getMessages(234, chatId, messageId, 2)
        messagesUser2.forEach { x -> assertTrue(x.readByAnotherUser) }
    }

    // endregion getMessages

    // region deleteChat

    @Test
    fun deleteChat_Simple(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteChat(123, chatService.getChatId(123, 234) as Int)

        assertTrue(res)
    }

    @Test
    fun deleteChat_UserNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteChat(666, chatService.getChatId(123, 234) as Int)

        assertFalse(res)
    }

    @Test
    fun deleteChat_ChatNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteChat(123, 666)

        assertFalse(res)
    }

    // endregion deleteChat

    // region deleteMessage

    @Test
    fun deleteMessage_Simple(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(125, 236, "asd")
        chatService.createMessage(125, 236, "qwe")

        var res = chatService.deleteMessage(125, chatService.getChatId(125, 236) as Int, messageId)

        assertTrue(res)
    }

    @Test
    fun deleteMessage_UserNotExists(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteMessage(666, chatService.getChatId(123, 234) as Int, messageId)

        assertFalse(res)
    }

    @Test
    fun deleteMessage_ChatNotExists(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteMessage(123, 666, messageId)

        assertFalse(res)
    }

    @Test
    fun deleteMessage_MessageNotExists(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(123, 234, "asd")

        var res = chatService.deleteMessage(123, chatService.getChatId(123, 234) as Int, messageId + 1)

        assertTrue(res)
    }

    @Test
    fun deleteMessage_EmptyChatWasDeleted(){
        val chatService = ChatService();
        var messageId = chatService.createMessage(123, 234, "asd")

        chatService.deleteMessage(123, chatService.getChatId(123, 234) as Int, messageId)
        var chatExists = chatService.getChats(123).any { x -> x.id == chatService.getChatId(123, 234) as Int }

        assertFalse(chatExists)
    }

    // endregion deleteMessage


    // region getUnreadChats

    @Test
    fun getUnreadChats_UnreadMessagesExist(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        chatService.getMessages(234, chatService.getChatId(123, 234) as Int, 0, 1)
        chatService.createMessage(567, 234, "xzc")
        chatService.createMessage(123, 234, "qwe")

        var chats = chatService.getUnreadChats(234)

        assertEquals(2, chats.size)
    }

    @Test
    fun getUnreadChats_AllMessagesRead(){
        val chatService = ChatService();
        var ind1 = chatService.createMessage(123, 234, "asd")
        var ind2 = chatService.createMessage(567, 234, "xzc")
        chatService.getMessages(234, chatService.getChatId(123, 234) as Int, ind1, 1)
        chatService.getMessages(234, chatService.getChatId(567, 234) as Int, ind2, 1)

        var chats = chatService.getUnreadChats(234)

        assertEquals(0, chats.size)
    }

    @Test
    fun getUnreadChats_UserNotExists(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")

        var chats = chatService.getUnreadChats(666)

        assertEquals(0, chats.size)
    }

    @Test
    fun getUnreadChats_NoChats(){
        var chats = ChatService().getUnreadChats(2345)

        assertEquals(0, chats.size)
    }

    // endregion getUnreadChats


    // region getUnreadChatsCount

    @Test
    fun getUnreadChatsCount_ChatsExist(){
        val chatService = ChatService();
        chatService.createMessage(123, 234, "asd")
        chatService.getMessages(234, chatService.getChatId(123, 234) as Int, 0, 1)
        chatService.createMessage(567, 234, "xzc")
        chatService.createMessage(123, 234, "qwe")

        var num = chatService.getUnreadChatsCount(234)

        assertEquals(2, num)
    }


    @Test
    fun getUnreadChatsCount_NoChats(){
        var chats = ChatService().getUnreadChats(666)

        assertEquals(0, chats.size)
    }

    // endregion getUnreadChatsCount
}