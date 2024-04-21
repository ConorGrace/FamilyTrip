package ie.setu.familytrip
import android.util.Log
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "LoginActivity"
class LoginActivity : AppCompatActivity() {

    private lateinit var setEmail: EditText
    private lateinit var setPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goPostsActivity()
        }

        setEmail = findViewById(R.id.setEmail)
        setPassword = findViewById(R.id.setPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = setEmail.text.toString()
            val password = setPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/Password can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goPostsActivity()
                } else {
                    Log.i(TAG, "signInWithEMail failed", task.exception)
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        btnSignUp.setOnClickListener {
           goSignUpActivity()
        }
    }

    private fun goPostsActivity() {
        Log.i(TAG, "goPostActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goSignUpActivity() {
        Log.i(TAG, "goSignUpActivity")
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}
