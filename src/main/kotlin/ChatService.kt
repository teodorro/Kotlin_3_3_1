import java.lang.IllegalArgumentException

class ChatService {
    private var chats = mutableListOf<Chat>()


    fun createMessage(userId: Int, userToId: Int, text: String): Int{
        var chat = chats.firstOrNull { x -> (x.userId1 == userId && x.userId2 == userToId) || (x.userId1 == userToId && x.userId2 == userId)  }
        if (chat == null) {
            chat = Chat(IdCreator.getNextId(), userId, userToId)
            chats.add(chat)
        }
        return chat.createMessage(userId, text)
    }

    fun getChats(userId: Int): Set<Chat>{
        return chats.filter { x -> x.userId1 == userId || x.userId2 == userId }.toSet()
    }

    fun getMessages(userId: Int, chatId: Int, messageStartFromId: Int, amount: Int): List<Message>{
        if (amount <= 0)
            throw IllegalArgumentException("Amount of messages should be over zero")
        var chat = chats.firstOrNull { x -> (x.userId1 == userId || x.userId2 == userId) && chatId == x.id }
        if (chat == null)
            return emptyList()
        if (chat.messages.all { x -> x.id != messageStartFromId })
            return emptyList()
        var messageInd = chat.messages.indexOfFirst { x -> x.id == messageStartFromId}
        var size = kotlin.math.min(amount, chat.messages.size - messageInd)

        chat.messages.asSequence().filter { x -> x.userWrittenById != userId }
            .forEach { x -> x.read(userId) }

        return chat.messages.subList(messageInd, messageInd + size)
    }

    fun deleteChat(userId: Int, chatId: Int): OperationResult{
        var chat: Chat? = chats.firstOrNull { x -> (x.userId1 == userId || x.userId2 == userId) && chatId == x.id }
            ?: return OperationResult.NotFound
        return if (chats.remove(chat)) OperationResult.Success else OperationResult.SmthWrong
    }

    fun deleteMessage(userId: Int, chatId: Int, messageId: Int): OperationResult{
        var chat = chats.firstOrNull { x -> (x.userId1 == userId || x.userId2 == userId) && chatId == x.id }
        if (chat == null)
            return OperationResult.NotFound
        val res = chat.deleteMessage(userId, messageId)
        if (res != OperationResult.Success)
            return res
        if (chat.messages.isEmpty())
            return if (chats.remove(chat)) OperationResult.Success else OperationResult.SmthWrong
        return res
    }

    fun getUnreadChats(userId: Int): Set<Chat> {
        var chats = getChats(userId);
        if (chats.isEmpty())
            return emptySet()
        return chats.asSequence().filter { x ->
            x.messages.last().userWrittenById != userId
                    && !x.messages.last().readByAnotherUser }.toSet()
    }

    fun getUnreadChatsCount(userId: Int): Int{
        return getUnreadChats(userId).size
    }

    fun getChatId(user1Id: Int, user2Id: Int): Int?{
        return chats.firstOrNull { x -> (x.userId1 == user1Id && x.userId2 == user2Id) || (x.userId1 == user2Id && x.userId2 == user1Id)}?.id
    }

    fun getLastMessages(userId: Int, chatId: Int, amount: Int): List<Message> {
        var chat = chats.firstOrNull { x -> (x.userId1 == userId || x.userId2 == userId) && chatId == x.id }
        if (chat == null)
            return emptyList()
        return chat.messages.takeLast(amount).toList()
    }

}