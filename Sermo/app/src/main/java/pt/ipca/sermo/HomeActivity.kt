package pt.ipca.sermo

import android.content.Intent
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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.adapters.ChatAdapter
import pt.ipca.sermo.models.Chat
import pt.ipca.sermo.models.ChatDto
import pt.ipca.sermo.models.Member

class HomeActivity : AppCompatActivity()
{
    private lateinit var username: String

    // Get field from XML
    private val contactET: EditText by lazy { findViewById<EditText>(R.id.home_contact_edittext) }
    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        username = intent.getStringExtra("Username")!!

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

        // Get Chat IDs of the chats where the user is in the members
        val db = Firebase.firestore
        val chatIds = mutableListOf<String>()
        val docRef = db.collectionGroup("Members").whereEqualTo("uid", userId)
        docRef.get().addOnCompleteListener { queryDocumentSnapshots ->
            for (doc in queryDocumentSnapshots.result.documents)
            {
                val chatId = doc.getString("chatId")
                chatIds.add(chatId!!)
            }

            // Create query of all the chats
            val docRefChats = db.collection("Chats").whereIn(FieldPath.documentId(), chatIds)

            // Add listener
            docRefChats.addSnapshotListener { result, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val chatsList: MutableList<ChatDto> = mutableListOf()
                for (document in result!!)
                {
                    val chatId = document.id

                    // Each user has a different title => the other members' names
                    var title = document.getString("title")

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
                    val username = documentFound.getString("username")
                    Toast.makeText(this,"User found", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "DocumentSnapshot data: ${documentFound.data}")

                    // Create chat with the new user
                    createChatDB(userId!!, username!!)
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

    private fun createChatDB(contactId: String, contactUsername: String)
    {
        val db = Firebase.firestore
        val batch = db.batch()
        val userId = Firebase.auth.currentUser!!.uid

        // Create Chat object
        val createdChat = Chat("Title",
            "LastMessage", "Time", false)

        // Add changes to the DB
        db.collection("Chats")
            .add(createdChat)
            .addOnSuccessListener { document ->
                val chatId = document.id

                // Create members
                val memberUser = Member(chatId, userId, username, false)
                val memberAdded = Member(chatId, contactId, contactUsername, false)
                val membersToAdd = listOf(memberUser, memberAdded)

                // Add members in one batch/write/update
                for (member in membersToAdd)
                {
                    // Automatically generate unique id
                    val docRefMembers = db.collection("Chats").document(chatId).
                    collection("Members").document()
                    batch.set(docRefMembers, memberUser)
                }
                batch.commit()

                Log.d(TAG, "DocumentSnapshot added with ID: ${document.id}")
                Toast.makeText(this,"New chat created!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error adding document", e)
            }
    }

    companion object { private const val TAG = "Home" }
}