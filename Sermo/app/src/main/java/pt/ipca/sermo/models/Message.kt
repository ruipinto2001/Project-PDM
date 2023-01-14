package pt.ipca.sermo.models

class Message(author: String, username: String, content: String, time: String, timestamp: String, state: String)
{
    public val Author = author
    public val Username = username
    public val Content = content
    public val Time = time
    public val Timestamp = timestamp
    public val State = state
}