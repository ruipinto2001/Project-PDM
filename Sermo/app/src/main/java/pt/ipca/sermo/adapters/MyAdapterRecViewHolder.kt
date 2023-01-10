package pt.ipca.sermo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.R

class MyAdapterRecViewHolder(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_row, parent, false))
{
    private var tvChatTitle: TextView? = itemView.findViewById(R.id.home_chatTitle)
    private var tvTimestamp: TextView? = itemView.findViewById(R.id.home_timestamp)
    private var tvLastMessage: TextView? = itemView.findViewById(R.id.home_lastMessage)

    fun bindData(chatTitle: String, timestamp: String, lastMessage: String)
    {
        tvChatTitle?.text = chatTitle
        tvTimestamp?.text = timestamp
        tvLastMessage?.text = lastMessage
    }
}