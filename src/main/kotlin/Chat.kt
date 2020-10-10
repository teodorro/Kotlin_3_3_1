class Chat(
    val id: Int,
    val userId1: Int,
    val userId2: Int
) {
    init {
        if (userId1 == userId2)
            throw IllegalArgumentException("userId1 should not be equal to userId2")
    }
    var messages = mutableListOf<Message>()

    fun createMessage(userId: Int, text: String): Int {
        if (text.isBlank())
            throw IllegalArgumentException("Message text should not be blank")
        if (userId != userId2 && userId != userId1)
            return -1

        val message = Message(IdCreator.getNextId(), userId, text)
        messages.add(message)

        messages.asSequence().filter { x -> x.userWrittenById != userId }
            .forEach { x -> x.read(userId) }

        return message.id
    }

    fun deleteMessage(userId: Int, messageId: Int): OperationResult {
        if (userId != userId2 && userId != userId1)
            return OperationResult.NotFound

        if (messages.any { x -> x.id == messageId }) {
            messages.remove(
                messages.first { x -> x.id == messageId })
            return OperationResult.Success
        }
        return OperationResult.NotFound
    }

    fun updateMessage(userId: Int, messageId: Int, text: String): OperationResult {
        if (text.isBlank())
            throw IllegalArgumentException("Message text should not be blank")
        if (userId != userId2 && userId != userId1)
            return OperationResult.NotFound

        if (messages.any { x -> x.id == messageId }) {
            messages.first { x -> x.id == messageId }.text = text
            return OperationResult.Success
        }
        return OperationResult.NotFound
    }
}