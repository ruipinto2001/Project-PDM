package pt.ipca.sermo.models

class Member(chatId: String, uid: String, username: String, isWriting: Boolean)
{
    public val ChatId = chatId
    public val Uid = uid
    public val Username = username
    public val IsWriting = isWriting
}