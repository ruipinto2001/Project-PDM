package pt.ipca.sermo.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.ChatActivity
import pt.ipca.sermo.R

class ChatAdapterViewHolder(inflater: LayoutInflater, val parent: ViewGroup,
                            private val username: String) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_row, parent, false))
{
    private var tvChatTitle: TextView? = itemView.findViewById(R.id.home_chatTitle)
    private var tvTimestamp: TextView? = itemView.findViewById(R.id.home_timestamp)
    private var tvLastMessage: TextView? = itemView.findViewById(R.id.home_lastMessage)

    fun bindData(chatId: String, chatTitle: String, timestamp: String, lastMessage: String)
    {
        tvChatTitle?.text = chatTitle
        tvTimestamp?.text = timestamp
        tvLastMessage?.text = lastMessage

        itemView.setOnClickListener{ v->
            val intent = Intent(v.context, ChatActivity::class.java)
            intent.putExtra("ChatId",chatId)
            intent.putExtra("Username", username)
            intent.putExtra("Contact", chatTitle)
            v.context.startActivity(intent)
        }
    }
}