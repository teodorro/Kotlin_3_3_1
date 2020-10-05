import org.junit.Assert.*
import org.junit.Test

class MessageTest {
    @Test
    fun read_NotChangedWhenSameUser(){
        val userWrittenById = 2
        val message = Message(1, userWrittenById, "asd")

        message.read(userWrittenById)

        assertFalse(message.readByAnotherUser)
    }

    @Test
    fun read_ChangedWhenAnotherUser(){
        val userWrittenById = 2
        val message = Message(1, userWrittenById, "asd")

        message.read(userWrittenById + 1)

        assertTrue(message.readByAnotherUser)
    }
}