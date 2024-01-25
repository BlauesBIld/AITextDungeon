package zeljko.dejan.rpginventorymanager

enum class ChatServiceState {
    WAITING_FOR_RESPONSE,
    WAITING_FOR_USER_INPUT,
    ERROR_CREATING_CHAT,
    ERROR_GETTING_NEXT_MESSAGE
}