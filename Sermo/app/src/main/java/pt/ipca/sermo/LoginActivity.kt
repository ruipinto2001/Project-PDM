package pt.ipca.sermo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.models.User


class LoginActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private val code = 1001
    private val buttonClick = AlphaAnimation(1f, 0.8f)

    // Get fields from XML
    private val emailET: EditText by lazy { findViewById<EditText>(R.id.login_email_edittext) }
    private val passwordET: EditText by lazy {
        findViewById<EditText>(R.id.login_password_edittext) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    fun doLogin(view: View)
    {
        // Logout
        auth.signOut()

        // Get the values of the XML fields
        val email = emailET.text.toString()
        val password = passwordET.text.toString()

        // Check if the user filled in all the fields
        if (TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter your email", Toast.LENGTH_LONG).show()
        else if (TextUtils.isEmpty(password))
            Toast.makeText(this,"Please enter your password", Toast.LENGTH_LONG).show()

        // Login
        else login(email, password)
        view.startAnimation(buttonClick);
    }

    fun startRegister(view: View)
    {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivityForResult(intent, code)
        view.startAnimation(buttonClick);
    }

    fun fast(view: View)
    {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code && data != null)
        {
            if (resultCode == Activity.RESULT_OK)
                Toast.makeText(this,"Register Successful!", Toast.LENGTH_LONG).show()
        }
    }

    private fun login(email: String, password: String)
    {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    // Get the uid of the current user
                    val userId = Firebase.auth.currentUser!!.uid

                    findUsernameById(userId)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "login:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun findUsernameById(userId: String)
    {
        val db = Firebase.firestore
        val docRef = db.collection("Users").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                // If the user was found
                if (document != null && document.exists())
                {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    Log.d(TAG, "login:success")
                    val username = document.getString("username")

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("Username", username)
                    startActivity(intent)
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

    companion object { private const val TAG = "Login" }
}