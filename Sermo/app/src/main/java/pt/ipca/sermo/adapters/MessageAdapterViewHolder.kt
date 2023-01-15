package pt.ipca.sermo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.R

class MessageAdapterViewHolder(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_message, parent, false))
{
    private var tvAuthor: TextView? = itemView.findViewById(R.id.message_author)
    private var tvContent: TextView? = itemView.findViewById(R.id.message_content)
    private var tvTime: TextView? = itemView.findViewById(R.id.message_time)
    private var ivCheck: ImageView? = itemView.findViewById(R.id.message_check)

    fun bindData(author: String, contactName: String, content: String, time: String, state: String)
    {
        tvAuthor?.text = contactName
        tvContent?.text = content
        tvTime?.text = time

        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid
        // Check if the message is from the user
        if (author == userId)
        {
            // Change check image if needed
            if (state == "received") ivCheck!!.setImageResource(R.drawable.tick)
            else if (state == "read") ivCheck!!.setImageResource(R.drawable.double_tick)
        }
    }
}