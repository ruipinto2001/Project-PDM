package pt.ipca.sermo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.sermo.models.Message

class MessageAdapter(private val mList: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MessageAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MessageAdapterViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MessageAdapterViewHolder, position: Int)
    {
        val author = mList.get(position).Author
        val contactName = mList.get(position).Username
        val content = mList.get(position).Content
        val time = mList.get(position).Time
        val state = mList.get(position).State

        holder.bindData(author, contactName, content, time, state)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}