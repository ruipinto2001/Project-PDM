package pt.ipca.sermo.models

class Message(authorId: String, authorUsername: String, content: String, time: String, timestamp: String, state: String)
{
    public val AuthorId = authorId
    public val AuthorUsername = authorUsername
    public val Content = content
    public val Time = time
    public val Timestamp = timestamp
    public val State = state
}