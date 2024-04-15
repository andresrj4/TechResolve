package activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R
import com.google.firebase.database.FirebaseDatabase
import android.util.Log


class activity_signup : AppCompatActivity() {
    private lateinit var loginContent: View
    private lateinit var signupContent: View

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var lastNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        loginContent = findViewById(R.id.login_content)
        signupContent = findViewById(R.id.signup_content)
        nameInput = findViewById(R.id.user_name_input)
        lastNameInput = findViewById(R.id.user_last_name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.password_confirmation_input)

        loginContent.visibility = View.GONE
        signupContent.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        val signupButton = findViewById<Button>(R.id.signup_btn)
        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            Log.d("activity_signup", "Email captured: $email")
            Log.d("activity_signup", "Password captured: $password")
            Log.d("activity_signup", "Button Clicked - Inputs: Name: $name, Last Name: $lastName, Email: $email, Password: $password, Confirm Password: $confirmPassword")

            if (!validateInputs(name, lastName, email, password, confirmPassword)) {
                Log.d("activity_signup", "Validation Failed - Some fields are incorrect")
            } else {
                registerUser(name, lastName, email, password)
            }
        }

        val signupLoginLink = findViewById<TextView>(R.id.signup_login_link)
        signupLoginLink.setOnClickListener {
            startActivity(Intent(this, activity_login::class.java))
            finish()
        }
    }

    private fun validateInputs(name: String, lastName: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            isValid = false
        }
        if (lastName.isEmpty()) {
            lastNameInput.error = "Last name is required"
            isValid = false
        }
        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            isValid = false
        }
        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            isValid = false
        }
        if (confirmPassword.isEmpty() || password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            isValid = false
        }
        return isValid
    }

    private fun registerUser(name: String, lastName: String, email: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        val userId = myRef.push().key
        val user = User(name, lastName, email, password)

        userId?.let {
            myRef.child(it).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to register user.", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    data class User(val name: String, val lastName: String, val email: String, val password: String)
}
