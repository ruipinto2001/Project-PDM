package pt.ipca.sermo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

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
}