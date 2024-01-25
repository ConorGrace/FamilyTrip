package ie.setu.familytrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "SignUpActivity"
class SignUpActivity : AppCompatActivity() {

    private lateinit var setEmail: EditText
    private lateinit var setPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val auth = FirebaseAuth.getInstance()

        setEmail = findViewById(R.id.setEmail)
        setPassword = findViewById(R.id.setPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            btnSignUp.isEnabled = false
            val email = setEmail.text.toString()
            val password = setPassword.text.toString()
            val confirmpassword = confirmPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/Password can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmpassword) {
                Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase authentication for login
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnSignUp.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goPostsActivity()
                } else {
                    Log.i(TAG, "CreateUserWithEmailAndPassword failed", task.exception)
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun goPostsActivity() {
        Log.i(TAG, "goPostActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
}