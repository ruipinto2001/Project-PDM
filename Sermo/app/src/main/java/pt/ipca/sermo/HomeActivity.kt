package pt.ipca.sermo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import pt.ipca.sermo.adapters.ChatAdapter
import pt.ipca.sermo.models.Chat
import pt.ipca.sermo.models.ChatDto

class HomeActivity : AppCompatActivity()
{
    // Get field from XML
    private val contactET: EditText by lazy { findViewById<EditText>(R.id.home_contact_edittext) }
    private lateinit var username: String

    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        username = intent.getStringExtra("Username")!!

        // RECYCLER VIEW
        val rvList = findViewById<RecyclerView>(R.id.home_chats_rv)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(this.resources.getDrawable(R.drawable.chat_divider))
        rvList.addItemDecoration(itemDecoration)

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

            val chatsList: MutableList<ChatDto> = mutableListOf()
            for (document in result!!)
            {
                val chatId = document.id

                var title = document.getString("title")
                // Each user has a different title => the other members' names
                // (if there is no user assigned title to the chat)
                if (title == "Title")
                {
                    val members = document.get("members") as List<String>
                    val usernames = document.get("usernames") as? List<String>
                    if (usernames != null)
                    {
                        if (members[0] == userId) title = usernames[1]
                        else title = usernames[0]
                    }
                }

                var lastMessage = document.getString("lastMessage")
                if (lastMessage == "LastMessage") lastMessage = "Say hi in the chat!"

                var time = document.getString("time")
                if (time == "Time") time = "Now? ^^"

                val importedChat = ChatDto(chatId, title!!, lastMessage!!, time!!)
                chatsList.add(importedChat)
                Log.d(TAG, "${document.id} => ${document.data}")
            }
            show(ChatAdapter(chatsList, username), rvList)
        }
    }

    fun show(adapter: ChatAdapter, recyclerView: RecyclerView)
    {
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.onFlingListener = null;
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
                    val contactName = documentFound.getString("username")
                    Log.d(TAG, "DocumentSnapshot data: ${documentFound.data}")

                    // Create chat with the new user
                    createChatDB(userId!!, contactName!!)
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

    private fun createChatDB(contactId: String, contactName: String)
    {
        val userId = Firebase.auth.currentUser!!.uid

        // Create list of chat members
        val members = listOf<String>(userId, contactId)

        // Create list of chat usernames
        val usernames = listOf<String>(username, contactName)

        // Create Chat object
        val createdChat = Chat(members, usernames, "Title",
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

    companion object { private const val TAG = "Home" }

    fun openProfile(view: View)
    {
        view.startAnimation(buttonClick);
        val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun getPushToken()
    {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            if (task.result != null && !TextUtils.isEmpty(task.result))
            {
                // Get new FCM registration token
                val token: String = task.result!!

                val msg = "InstanceID Token: "+token
                Log.d("Main", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        })
    }
}