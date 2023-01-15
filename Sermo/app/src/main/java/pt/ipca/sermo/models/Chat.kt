package pt.ipca.sermo.models

class Chat(members: List<String>, usernames: List<String>, title: String, lastMessage: String,
           time: String, groupChat: Boolean)
{
    public val Members = members
    public val Usernames = usernames
    public val Title = title
    public val LastMessage = lastMessage
    public val Time = time
    public val GroupChat = groupChat
}