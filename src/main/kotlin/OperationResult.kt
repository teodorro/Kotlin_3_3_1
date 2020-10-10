sealed class OperationResult {
    object Success : OperationResult()
    object NotFound : OperationResult()
    object SmthWrong : OperationResult()
}