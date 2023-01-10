package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.adapters.MyAdapterRec
import pt.ipca.sermo.models.Chat
import pt.ipca.sermo.models.ChatDto
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity()
{
    // Get field from XML
    private val contactET: EditText by lazy { findViewById<EditText>(R.id.home_contact_edittext) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // RECYCLER VIEW
        val rvList = findViewById<RecyclerView>(R.id.home_chats_rv)

        // GET ALL CHATS
        getAllChats(rvList)
    }

    private fun getAllChats(rvList: RecyclerView)
    {
        val db = Firebase.firestore
        val docRef = db.collection("Chats")
        docRef.get()
            .addOnSuccessListener { result ->
                var chatsList: MutableList<ChatDto> = mutableListOf()
                for (document in result)
                {
                    val title = document.getString("title")
                    val lastMessage = document.getString("lastMessage")
                    val timestamp = document.getString("timestamp")
                    val importedChat = ChatDto(title!!, lastMessage!!, timestamp!!)
                    chatsList.add(importedChat)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                show(MyAdapterRec(chatsList), rvList)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun show(adapter: MyAdapterRec, recyclerView: RecyclerView)
    {
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        PagerSnapHelper().attachToRecyclerView(recyclerView) // snap pics
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
        // Check if user exists
        else findUserByEmail(contactEmail)
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
        val createdChat = Chat(members, "Title", "LastMessage", "Timestamp")

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

    private fun getCurrentFormattedDateTime(): String
    {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val formattedDateTime = formatter.format(time)

        return formattedDateTime
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

    /*
    private fun addNewContactDB(db: FirebaseFirestore, userId: String,
                                contactId: String, contactUsername: String)
    {
        val newContact = Contact(contactId)

        // Add the new contact ID to the user's contact list
        db.collection("Users")
            .document(userId)
            .collection("Contacts")
            .document(contactId)
            .set(newContact)
            .addOnSuccessListener { _ ->
                Log.d(TAG,"DocumentSnapshot added with ID: $contactId")
                Toast.makeText(this,"New contact added!", Toast.LENGTH_SHORT).show()
                addNewChatDB(db, userId, contactId, contactUsername)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    private fun addNewChatDB(db: FirebaseFirestore, userId: String,
                     contactId: String, contactUsername: String)
    {
        // Format values
        val lastMessage = "Say hello to $contactUsername!"
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val lastMsgTimestamp = formatter.format(time)

        // Create Chat object
        val createdChat = Chat(contactUsername, lastMessage, lastMsgTimestamp)

        // Add chat to the DB
        db.collection("Chats")
            .add(createdChat)
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${document.id}")
                Toast.makeText(this,"New chat created!", Toast.LENGTH_SHORT).show()
                addChatMembersDB(db, document.id, userId, contactId)
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error adding document", e)
            }

    }

    private fun addChatMembersDB(db: FirebaseFirestore, chatId: String,
                                 userId: String, contactId: String)
    {
        val newMember1 = Contact(userId)
        val newMember2 = Contact(contactId)

        // Add a new member to the chat
        db.collection("Chats")
            .document(chatId)
            .collection("Members")
            .document(userId)
            .set(newMember1)
            .addOnSuccessListener { _ ->
                Log.d(TAG,"DocumentSnapshot added with ID: $contactId")
                Toast.makeText(this,"New member added to the chat", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

        // Add a new member to the chat
        db.collection("Chats")
            .document(chatId)
            .collection("Members")
            .document(contactId)
            .set(newMember2)
            .addOnSuccessListener { _ ->
                Log.d(TAG,"DocumentSnapshot added with ID: $contactId")
                Toast.makeText(this,"New member added to the chat", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }
    */

    companion object { private const val TAG = "Home" }
}