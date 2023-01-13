package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.adapters.ChatAdapter
import pt.ipca.sermo.models.Chat
import pt.ipca.sermo.models.ChatDto

class HomeActivity : AppCompatActivity()
{
    // Get field from XML
    private val contactET: EditText by lazy { findViewById<EditText>(R.id.home_contact_edittext) }
    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // RECYCLER VIEW
        val rvList = findViewById<RecyclerView>(R.id.home_chats_rv)
        rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // GET ALL CHATS
        chatListener(rvList)
    }

    private fun chatListener(rvList: RecyclerView)
    {
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid

        val db = Firebase.firestore
        val docRef = db.collection("Chats").whereArrayContains("members", userId)
        docRef.addSnapshotListener { result, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            var chatsList: MutableList<ChatDto> = mutableListOf()
            for (document in result!!)
            {
                val chatId = document.id

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

                var time = document.getString("time")
                if (time == "Time") time = "Now? ^^"

                val importedChat = ChatDto(chatId, title!!, lastMessage!!, time!!)
                chatsList.add(importedChat)
                Log.d(TAG, "${document.id} => ${document.data}")
            }
            show(ChatAdapter(chatsList), rvList)
        }
    }

    fun show(adapter: ChatAdapter, recyclerView: RecyclerView)
    {
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setOnFlingListener(null);
    }

    fun addNewContact(view: View)
    {
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid
        // Get the value of the XML field
        val contactEmail = contactET.text.toString()

        // Check if the user filled in the field
        if (TextUtils.isEmpty(contactEmail))
            Toast.makeText(this,"Please write the ID of the new contact!", Toast.LENGTH_LONG).show()
        else
        {
            // Remove text written in contact box
            contactET.setText("")
            // Check if user exists
            findUserByEmail(contactEmail)
        }
        view.startAnimation(buttonClick);
    }

    private fun findUserByEmail(userEmail: String)
    {
        val db = Firebase.firestore
        val docRef = db.collection("Users").whereEqualTo("email", userEmail).limit(1)
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                // If the user was found
                if (!querySnapshot.isEmpty)
                {
                    val documentFound = querySnapshot.documents[0]
                    val userId = documentFound.getString("uid")
                    Toast.makeText(this,"User found", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "DocumentSnapshot data: ${documentFound.data}")

                    // Create chat with the new user
                    createChatDB(userId!!)
                } else
                {
                    Log.d(TAG, "No such document")
                    Toast.makeText(this,"User not found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun createChatDB(contactId: String)
    {
        val userId = Firebase.auth.currentUser!!.uid

        // Create list of chat members
        val members = listOf<String>(userId, contactId)

        // Create Chat object
        val createdChat = Chat(members, "Title",
            "LastMessage", "Time", false)

        // Add chat to the DB
        val db = Firebase.firestore
        db.collection("Chats")
            .add(createdChat)
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${document.id}")
                Toast.makeText(this,"New chat created!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error adding document", e)
            }
    }

    private fun findUserById(db: FirebaseFirestore, userId: String, contactId: String)
    {
        val docRef = db.collection("Users").document(contactId)
        docRef.get()
            .addOnSuccessListener { document ->
                // If the user was found
                if (document != null && document.exists())
                {
                    val contactUsername = document.getString("username")
                    Toast.makeText(this,"User found", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                }
                else
                {
                    Log.d(TAG, "No such document")
                    Toast.makeText(this,"User not found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    companion object { private const val TAG = "Home" }
}