package pt.ipca.sermo

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.adapters.MessageAdapter
import pt.ipca.sermo.models.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity()
{
    var active = false

    // Get field from XML
    private val messageBoxET: EditText by lazy {
        findViewById<EditText>(R.id.chat_messageBox_edittext) }

    private lateinit var chatId: String
    private lateinit var username: String
    private lateinit var contact: String
    private var messageCount = 0

    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        username = intent.getStringExtra("Username")!!
        contact = intent.getStringExtra("Contact")!!
        chatId = intent.getStringExtra("ChatId")!!

        // RECYCLER VIEW
        val rvList = findViewById<RecyclerView>(R.id.chat_messages_rv)
        // Separate list items with space and line
        rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        // Adjust list when keyboard opens
        rvList.addOnLayoutChangeListener(OnLayoutChangeListener {
                view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                rvList.postDelayed(
                    Runnable { rvList.scrollToPosition(messageCount - 1) },
                    100
                )
            }
        })

        // Contact name (on top)
        val tvContactName = findViewById<TextView>(R.id.chat_contact_textview)
        tvContactName.text = contact

        // GET ALL CHATS
        messageListener(rvList)
    }

    private fun messageListener(rvList: RecyclerView)
    {
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid

        val db = Firebase.firestore
        val docRef = db.collection("Chats").document(chatId).
                        collection("Messages").orderBy("timestamp")
        docRef.addSnapshotListener { result, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val messageList: MutableList<Message> = mutableListOf()
            for (document in result!!)
            {
                val author = document.getString("author")
                val contactUsername = document.getString("username")
                val content = document.getString("content")
                val time = document.getString("time")
                val timestamp = document.getString("timestamp")
                val state = document.getString("state")

                // Check if the message is from another user and is not assigned as "read" yet
                // (only if the user is currently in the chat = active)
                if (active && author != userId && state != "read")
                {
                    // Update message state
                    val docRefUpdate = db.collection("Chats").document(chatId).
                                        collection("Messages").document(document.id)
                    docRefUpdate.update("state", "read")
                }

                val importedMessage = Message(author!!, contactUsername!!, content!!, time!!, timestamp!!, state!!)
                messageList.add(importedMessage)
                Log.d(TAG, "${document.id} => ${document.data}")
            }
            show(MessageAdapter(messageList), rvList)
        }
    }

    private fun show(adapter: MessageAdapter, recyclerView: RecyclerView)
    {
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.onFlingListener = null;
        messageCount = adapter.itemCount
        recyclerView.scrollToPosition(messageCount - 1)
    }

    fun sendNewMessage(view: View)
    {
        // Get the value of the XML field
        val content = messageBoxET.text.toString()

        // Check if the user filled in the field
        if (TextUtils.isEmpty(content))
            Toast.makeText(this,"Please write something before sending a message!",
                Toast.LENGTH_LONG).show()
        else
        {
            // Remove text written in message box (to make it easier to send another message)
            messageBoxET.setText("")

            // Get timestamp
            val timestamp = System.currentTimeMillis().toString()

            // Get current formatted date and time
            val time = getCurrentFormattedDateTime()

            // Get the uid of the current user
            val userId = Firebase.auth.currentUser!!.uid

            // Create message object
            val createdMessage = Message(userId, username, content, time, timestamp, "sent")

            // Add message to the DB
            val db = Firebase.firestore
            val docRefChat = db.collection("Chats").document(chatId)
            val docRefMessages = docRefChat.collection("Messages")

            docRefMessages.add(createdMessage)
                .addOnSuccessListener { document ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${document.id}")
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error adding document", e)
                }

            // Update chat
            docRefChat.update("lastMessage", content, "time", time)
        }
    }

    private fun getCurrentFormattedDateTime(): String
    {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm")
        val formattedDateTime = formatter.format(time)

        return formattedDateTime
    }

    companion object { private const val TAG = "Chat" }
}