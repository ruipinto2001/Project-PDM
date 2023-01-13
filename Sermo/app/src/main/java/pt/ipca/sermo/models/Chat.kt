package pt.ipca.sermo.models

class Chat(title: String, lastMessage: String,
           time: String, groupChat: Boolean)
{
    public val Title = title
    public val LastMessage = lastMessage
    public val Time = time
    public val GroupChat = groupChat
}