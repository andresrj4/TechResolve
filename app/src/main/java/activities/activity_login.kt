package activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class activity_login : AppCompatActivity() {
    private lateinit var loginContent: View
    private lateinit var signupContent: View
    private lateinit var auth: FirebaseAuth

    lateinit var emailInput_login : EditText
    lateinit var passwordInput_login : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginContent = findViewById(R.id.login_content)
        signupContent = findViewById(R.id.signup_content)
        emailInput_login = findViewById(R.id.emailinput_login)
        passwordInput_login = findViewById(R.id.passwordinput_login)
        loginBtn = findViewById(R.id.login_btn)

        loginContent.visibility = View.VISIBLE
        signupContent.visibility = View.GONE

        loginBtn.setOnClickListener {
            val email = emailInput_login.text.toString().trim()
            val password = passwordInput_login.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty.", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        val createAccountText = findViewById<TextView>(R.id.signup_login_link)
        createAccountText.setOnClickListener {
            startActivity(Intent(this, activity_signup::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Log in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navigate to Home Activity
            startActivity(Intent(this, activity_app::class.java))
            finish()
        } else {
            // Optionally clear input fields
            emailInput_login.text.clear()
            passwordInput_login.text.clear()
        }
    }

}
