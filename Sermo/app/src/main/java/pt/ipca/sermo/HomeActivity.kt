package pt.ipca.sermo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity()
{
    // Get field from XML
    private val contactET: EditText by lazy { findViewById<EditText>(R.id.home_contact_edittext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun addNewContact(view: View)
    {
        // Get the value of the XML field
        val newContactID = contactET.text.toString()

        // Check if the user filled in the field
        if (TextUtils.isEmpty(newContactID))
            Toast.makeText(this,"Please write the ID of the new contact!", Toast.LENGTH_LONG).show()
        else addNewContactDB(newContactID)
    }

    private fun addNewContactDB(uid: String)
    {
        val user = Firebase.auth.currentUser

        // TODO: check if the user exists
        // TODO: create new chat in the home screen

        // Add the new contact ID to the user's contact list
        /*val db = Firebase.firestore
        db.collection("Users")
            .document(user!!.uid)
            .collection("Contacts")
            .document(uid)
            .set(uid)
            .addOnSuccessListener { _ ->
                Log.d(TAG,"DocumentSnapshot added with ID: $uid")
                Toast.makeText(this,"New contact added!", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }*/
    }

    companion object { private const val TAG = "Home" }
}