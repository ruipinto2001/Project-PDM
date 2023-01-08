package pt.ipca.sermo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.ipca.sermo.models.User

class LoginActivity : AppCompatActivity() {
    private val code = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun doLogin(view: View) {
        // Placeholder message
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
    }

    fun startRegister(view: View)
    {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code && data != null)
        {
            if (resultCode == Activity.RESULT_OK)
                Toast.makeText(this,"Register Successful!", Toast.LENGTH_LONG).show()
        }
    }

}