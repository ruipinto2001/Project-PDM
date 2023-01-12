package pt.ipca.sermo.models

class Chat(members: List<String>, title: String, lastMessage: String,
           timestamp: String, groupChat: Boolean)
{
    public val Members = members
    public val Title = title
    public val LastMessage = lastMessage
    public val Timestamp = timestamp
    public val GroupChat = groupChat
}

// TODO: when a message is sent, add attributes to the chat -> lastMessage + lastMsgTimestamp