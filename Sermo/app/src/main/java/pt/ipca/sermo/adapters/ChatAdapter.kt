package pt.ipca.sermo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.models.ChatDto

class ChatAdapter(private val mList: MutableList<ChatDto>) : RecyclerView.Adapter<ChatAdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatAdapterViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int)
    {
        val chatId = mList.get(position).ChatId
        val chatTitle = mList.get(position).Title
        val timestamp = mList.get(position).Time
        val lastMessage = mList.get(position).LastMessage
        holder.bindData(chatId, chatTitle, timestamp, lastMessage)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}