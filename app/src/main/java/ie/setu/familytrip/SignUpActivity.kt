package ie.setu.familytrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.familytrip.models.User

private const val TAG = "SignUpActivity"
class SignUpActivity : AppCompatActivity() {

    private lateinit var setEmail: EditText
    private lateinit var setUsername: EditText
    private lateinit var setCountry: EditText
    private lateinit var setPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val auth = FirebaseAuth.getInstance()

        setEmail = findViewById(R.id.setEmail)
        setUsername = findViewById(R.id.setUsername)
        setCountry = findViewById(R.id.setCountry)
        setPassword = findViewById(R.id.setPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            btnSignUp.isEnabled = false
            val email = setEmail.text.toString()
            val username = setUsername.text.toString()
            val country = setCountry.text.toString()
            val password = setPassword.text.toString()
            val confirmpassword = confirmPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/Password can't be empty", Toast.LENGTH_SHORT).show()
                btnSignUp.isEnabled = true
                return@setOnClickListener
            }
            if (password != confirmpassword) {
                Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show()
                btnSignUp.isEnabled = true
                return@setOnClickListener
            }

            // Firebase authentication for login code, using the email and password fields as the user creation tools
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnSignUp.isEnabled = true
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // User is signed into app, and added as a part of the model
                        val newUser =
                            User(username, country, user.uid)
                        addUserDataToFirestore(newUser)
                    }
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goPostActivity()
                } else {
                    Log.i(TAG, "CreateUserWithEmailAndPassword failed", task.exception)
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goPostActivity() {
        Log.i(TAG, "goPostActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addUserDataToFirestore(user: User) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)  // We use the users uuid that was generated before to stay logged in
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User data added to Firestore successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding user data to Firestore", e)
            }
    }
}