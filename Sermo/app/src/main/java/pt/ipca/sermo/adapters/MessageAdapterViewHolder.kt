package pt.ipca.sermo.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.ChatActivity
import pt.ipca.sermo.R

class MessageAdapterViewHolder(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_message, parent, false))
{
    private var tvAuthor: TextView? = itemView.findViewById(R.id.message_author)
    private var tvContent: TextView? = itemView.findViewById(R.id.message_content)
    private var tvTime: TextView? = itemView.findViewById(R.id.message_time)

    fun bindData(author: String, content: String, time: String)
    {
        tvAuthor?.text = author
        tvContent?.text = content
        tvTime?.text = time
    }
}