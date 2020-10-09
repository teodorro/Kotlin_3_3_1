object IdCreator {
    private var nextId = 1

    fun getNextId(): Int{
        return nextId++
    }
}