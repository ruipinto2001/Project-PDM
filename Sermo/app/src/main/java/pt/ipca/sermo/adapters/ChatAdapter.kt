package pt.ipca.sermo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.models.ChatDto

class ChatAdapter(private val mList: MutableList<ChatDto>) : RecyclerView.Adapter<ChatAdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatAdapterViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int)
    {
        val chatId = mList.get(position).ChatId
        var chatTitle = mList.get(position).Title
        val timestamp = mList.get(position).Time
        val lastMessage = mList.get(position).LastMessage

        // If there is no user assigned title to the chat
        if (chatTitle == "Title")
        {
            chatTitle = ""

            // Get the uid of the current user
            val userId = Firebase.auth.currentUser!!.uid

            // Get members
            val db = Firebase.firestore
            val docRef = db.collection("Chats").document(chatId).
                            collection("Members")
            docRef.get().
                addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("Home", "${document.id} => ${document.data}")
                        val memberId = document.getString("Uid")
                        if (memberId != userId)
                        {
                            val memberUsername = document.getString("Username")
                            chatTitle += "$memberUsername "
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Home", "Error getting documents: ", exception)
                }


        }

        holder.bindData(chatId, chatTitle, timestamp, lastMessage)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}