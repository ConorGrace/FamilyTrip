package ie.setu.familytrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class LoginActivity : AppCompatActivity() {
    private lateinit var setEmail: EditText
    private lateinit var setPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find views by their IDs
        setEmail = findViewById(R.id.setEmail)
        setPassword = findViewById(R.id.setPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = setEmail.text.toString()
            val password = setPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/Password can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Firebase authentication check
        }
    }
}
