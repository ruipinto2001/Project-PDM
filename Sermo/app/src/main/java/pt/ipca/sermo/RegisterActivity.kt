package pt.ipca.sermo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun doRegister(view: View)
    {
        // Placeholder message
        Toast.makeText(this, "Register Successful!", Toast.LENGTH_LONG).show()
    }

}