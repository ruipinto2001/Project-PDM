package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.adapters.MyAdapterRec
import pt.ipca.sermo.models.ChatDto
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity()
{
    // Get field from XML
    private val messageBoxET: EditText by lazy {
        findViewById<EditText>(R.id.chat_messageBox_edittext) }

    lateinit private var chatId: String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // RECYCLER VIEW
        val rvList = findViewById<RecyclerView>(R.id.chat_messages_rv)

        chatId = intent.getStringExtra("ChatId")!!

        // GET ALL CHATS
        //messageListener(rvList)
    }

    private fun messageListener(rvList: RecyclerView)
    {/*
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid

        val db = Firebase.firestore
        val docRef = db.collection("Messages").whereArrayContains("members", userId)
        docRef.addSnapshotListener { result, e ->
            if (e != null) {
                Log.w(HomeActivity.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            var chatsList: MutableList<ChatDto> = mutableListOf()
            for (document in result!!)
            {
                var title = document.getString("title")
                // Each user has a different title => the other members' names
                // (if there is no user assigned title to the chat)
                if (title == "Title")
                {
                    title = ""
                    val members = document.get("members") as List<String>
                    for (m in members) if (m != userId) title += "$m "
                }

                var lastMessage = document.getString("lastMessage")
                if (lastMessage == "LastMessage") lastMessage = "Say hi in the chat!"

                var timestamp = document.getString("timestamp")
                if (timestamp == "Timestamp") timestamp = "Now? ^^"

                val importedChat = ChatDto(title!!, lastMessage!!, timestamp!!)
                chatsList.add(importedChat)
                Log.d(HomeActivity.TAG, "${document.id} => ${document.data}")
            }
            show(MyAdapterRec(chatsList), rvList)
        }*/
    }

    fun sendNewMessage(view: View)
    {
        // Get the value of the XML field
        val message = messageBoxET.text.toString()

        // Check if the user filled in the field
        if (TextUtils.isEmpty(message))
            Toast.makeText(this,"Please write something before sending a message!",
                Toast.LENGTH_LONG).show()
        //else
    }

    private fun getCurrentFormattedDateTime(): String
    {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val formattedDateTime = formatter.format(time)

        return formattedDateTime
    }
}