package activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R

class activity_signup : AppCompatActivity() {
    private lateinit var loginContent: View
    private lateinit var signupContent: View

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var emailAvailabilityIndicator: TextView
    private lateinit var passwordMatchIndicator: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        loginContent = findViewById(R.id.login_content)
        signupContent = findViewById(R.id.signup_content)

        loginContent.visibility = View.GONE
        signupContent.visibility = View.VISIBLE

        val signupButton = findViewById<Button>(R.id.signup_btn)
        val nameInput = findViewById<EditText>(R.id.user_name_input)
        val lastNameInput = findViewById<EditText>(R.id.user_last_name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.password_confirmation_input)
        // emailAvailabilityIndicator = findViewById(R.id.email_availability_indicator)
        // passwordMatchIndicator = findViewById(R.id.password_match_indicator)

        emailInput.addTextChangedListener(emailTextWatcher)
        passwordInput.addTextChangedListener(passwordTextWatcher)

        signupButton.setOnClickListener {
            val name = nameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            // Validate inputs and proceed with registration
            if (validateInputs(name, lastName, email, password, confirmPassword)) {
                // Call method to register user in the database
                registerUser(name, lastName, email, password)
            }
        }

        val signupLoginLink = findViewById<TextView>(R.id.signup_login_link)
        signupLoginLink.setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val email = s.toString()
            // Implement logic to check email availability in the database
            checkEmailAvailability(email)
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val password = s.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            // Implement logic to check password and confirm password match
            checkPasswordMatch(password, confirmPassword)
        }
    }

    private fun checkEmailAvailability(email: String) {
        // emailAvailabilityIndicator.text = if (isEmailAvailable(email)) "✔️" else "❌"
    }

    private fun checkPasswordMatch(password: String, confirmPassword: String) {
        val match = password == confirmPassword
        passwordMatchIndicator.text = if (match) "✔️" else "❌"
    }

    private fun validateInputs(
        name: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Return true if inputs are valid, false otherwise
        return true // PH
    }

    private fun registerUser(name: String, lastName: String, email: String, password: String) {
        // val sql = "INSERT INTO users (name, last_name, email, password) VALUES ('$name', '$lastName', '$email', '$password')"
        // Execute the SQL query using JDBC's Statement or PreparedStatement
    }
}
