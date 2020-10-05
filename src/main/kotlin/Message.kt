class Message (
    val id: Int,
    val userWrittenById: Int,
    var text: String,
){
    var readByAnotherUser: Boolean = false
    private set

    fun read(userId: Int){
        if (userId != userWrittenById)
            readByAnotherUser = true
    }
}