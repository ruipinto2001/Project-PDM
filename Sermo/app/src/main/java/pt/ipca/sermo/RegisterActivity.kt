package pt.ipca.sermo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.models.User

class RegisterActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth

    // Get fields from XML
    private val usernameET: EditText by lazy {
        findViewById<EditText>(R.id.register_username_edittext) }
    private val emailET: EditText by lazy { findViewById<EditText>(R.id.register_email_edittext) }
    private val passwordET: EditText by lazy {
        findViewById<EditText>(R.id.register_password_edittext) }
    private val confirmPasswordET: EditText by lazy {
        findViewById<EditText>(R.id.register_confirmPassword_edittext) }
    private val genderSP : Spinner by lazy { findViewById<Spinner>(R.id.register_gender_spinner) }
    private val birthdayDP: DatePicker by lazy {
        findViewById<DatePicker>(R.id.register_birthday_datepicker) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Setup the gender spinner (simple_spinner_item)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Male","Female","Other"))
        genderSP.adapter = spinnerAdapter
        genderSP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {}
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun doRegister(view: View)
    {
        // Get the values of the XML fields
        val username = usernameET.text.toString()
        val email = emailET.text.toString()
        val password = passwordET.text.toString()
        val confirmPassword = confirmPasswordET.text.toString()
        val gender = genderSP.selectedItem.toString()
        val day = birthdayDP.dayOfMonth.toString()
        val month = birthdayDP.month.toString()
        val year = birthdayDP.year.toString()

        // Format birthday date
        val birthday = "$day/$month/$year"

        // Check if the user filled in all the fields
        if (TextUtils.isEmpty(username))
            Toast.makeText(this,"Please enter a username", Toast.LENGTH_LONG).show()
        else if (TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter an email", Toast.LENGTH_LONG).show()
        else if (TextUtils.isEmpty(password))
            Toast.makeText(this,"Please enter a password", Toast.LENGTH_LONG).show()
        else if (TextUtils.isEmpty(confirmPassword))
            Toast.makeText(this,"Please confirm your password", Toast.LENGTH_LONG).show()

        // Confirm password input (password and confirm password must match)
        else if (password != confirmPassword)
            Toast.makeText(this,"Passwords don't match", Toast.LENGTH_LONG).show()

        // Create account
        else createAccount(username, email, password, gender, birthday)
    }

    // Validates email and password and creates a new user
    private fun createAccount(username: String, email: String, password: String,
                              gender: String, birthday: String): String
    {
        var uid = ""

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
                if (task.isSuccessful)
                {
                    Log.d(TAG, "createAccount:success")

                    // Send new user data to Firebase Cloud Firestore
                    registerDB(username, gender, birthday)
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createAccount:failure", task.exception)
                    Toast.makeText(this, "Authentication failed",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
        return uid
    }

    private fun validateEmail(email: String): Boolean
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private fun registerDB(username: String, gender: String, birthday: String)
    {
        val user = Firebase.auth.currentUser

        // If the registration was successful, pass the new user data to the DB
        if (user != null)
        {
            val db = Firebase.firestore
            val createdUser = User(user.uid, username, user.email!!, gender, birthday)

            // Add user to the DB
            db.collection("Users")
                .document(createdUser.Uid)
                .set(createdUser)
                .addOnSuccessListener { _ ->
                    Log.d(TAG,
                        "DocumentSnapshot added with ID: ${createdUser.Uid}")
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish() // Exit register activity
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error adding document", e) }
        }
    }

    companion object { private const val TAG = "Register" }
}