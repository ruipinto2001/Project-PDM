package pt.ipca.sermo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.models.ChatDto

class MyAdapterRec(private val mList: MutableList<ChatDto>) : RecyclerView.Adapter<MyAdapterRecViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterRecViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyAdapterRecViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyAdapterRecViewHolder, position: Int)
    {
        val chatId = mList.get(position).ChatId
        val chatTitle = mList.get(position).Title
        val timestamp = mList.get(position).Timestamp
        val lastMessage = mList.get(position).LastMessage
        holder.bindData(chatId, chatTitle, timestamp, lastMessage)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}