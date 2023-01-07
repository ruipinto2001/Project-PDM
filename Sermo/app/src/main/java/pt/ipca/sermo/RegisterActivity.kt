package pt.ipca.sermo

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private val emailET: EditText by lazy { findViewById<EditText>(R.id.register_email_edittext) }
    private val passwordET: EditText by lazy {
        findViewById<EditText>(R.id.register_password_edittext) }
    private val confirmPasswordET: EditText by lazy {
        findViewById<EditText>(R.id.register_confirmPassword_edittext) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    fun doRegister(view: View)
    {
        val email = emailET.text.toString()
        val password = passwordET.text.toString()
        val confirmPassword = confirmPasswordET.text.toString()

        // Confirm password input (password and confirm password must match)
        if (password != confirmPassword)
            Toast.makeText(this,"Passwords don't match", Toast.LENGTH_LONG).show()
        else
        {
            createAccount(email, password)

            // TODO: Save user registration data on DB
        }
    }

    // Validates email and password and creates a new user
    private fun createAccount(email: String, password: String)
    {
        // Invalid e-mail
        if (!validateEmail(email))
            Toast.makeText(this,"Please enter a valid e-mail address",
                Toast.LENGTH_LONG).show()

        // Invalid password
        else if (!validatePassword(password))
            Toast.makeText(this,
                "Passwords must be at least 6 characters long and " +
                        "have to contain 1 uppercase letter, " +
                        "1 lowercase letter and " +
                        "1 special character",
                Toast.LENGTH_LONG).show()

        // Valid fields -> start registration
        else
        {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
            { task -> // Sign in success
                if (task.isSuccessful) Log.d(TAG, "createUserWithEmail:success")
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean
    {
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private fun validatePassword(password: String): Boolean
    {
        // At least one lowercase letter: (?=.*[a-z])
        // At least one uppercase letter: (?=.*[A-Z])
        // At least one special character: (?=.*[@#$%!\-_?&])
        // Minimum of 6 characters: {6,}
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%!\\-_?&])(?=\\S+\$).{6,}".toRegex()

        return password.matches(passwordRegex)
    }

}