package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity()
{
    // Get fields from XML
    private val usernameTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_username_textview) }
    private val emailTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_email_textview) }
    private val birthdayTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_birthday_textview) }
    private val genderTV: TextView by lazy {
        findViewById<TextView>(R.id.profile_gender_textview) }

    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        loadUserInfo()
    }

    fun closeProfile(view: View)
    {
        view.startAnimation(buttonClick);
        finish()
    }

    private fun loadUserInfo()
    {
        // Get the uid of the current user
        val userId = Firebase.auth.currentUser!!.uid

        val db = Firebase.firestore
        val docRef = db.collection("Users").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                // If the user was found
                if (document != null && document.exists())
                {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                    val username = document.getString("username")
                    usernameTV.text = username

                    val email = document.getString("email")
                    emailTV.text = email

                    val birthday = document.getString("birthday")
                    birthdayTV.text = birthday

                    val gender = document.getString("gender")
                    genderTV.text = gender

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

    companion object { private const val TAG = "Profile" }
}