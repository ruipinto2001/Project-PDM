package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun doLogin(view: View) {
        // Placeholder message
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
    }

    fun startRegister(view: View) {
        // Placeholder message
        Toast.makeText(this, "Register Successful!", Toast.LENGTH_LONG).show()
    }
}