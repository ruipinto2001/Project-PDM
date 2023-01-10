package pt.ipca.sermo.models

class Chat(members: List<String>, title: String, lastMessage: String, timestamp: String)
{
    public val Members = members
    public val Title = title
    public val LastMessage = lastMessage
    public val Timestamp = timestamp
}

// TODO: when a message is sent, add attributes to the chat -> lastMessage + lastMsgTimestamp