package activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R

class ActivitySignup : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var lastNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        findViewById<View>(R.id.login_content).visibility = View.GONE
        findViewById<View>(R.id.signup_content).visibility = View.VISIBLE

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        nameInput = findViewById(R.id.user_name_input)
        lastNameInput = findViewById(R.id.user_last_name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.password_confirmation_input)
    }

    private fun setupClickListeners() {
        val signupButton = findViewById<Button>(R.id.signup_btn)
        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (!validateInputs(name, lastName, email, password, confirmPassword)) {
                return@setOnClickListener
            } else {
                UserManager.getInstance().registerUser(email, password, name, lastName, {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_LONG).show()
                }, { errorMsg ->
                    Toast.makeText(this, "Registration failed: $errorMsg", Toast.LENGTH_SHORT).show()
                })
            }
        }

        findViewById<TextView>(R.id.signup_login_link_text).setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(name: String, lastName: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            nameInput.error = "Nombre requerido"
            isValid = false
        }
        if (lastName.isEmpty()) {
            lastNameInput.error = "Apellidos requeridos"
            isValid = false
        }
        if (email.isEmpty()) {
            emailInput.error = "Correo requerido"
            isValid = false
        }
        if (password.isEmpty()) {
            passwordInput.error = "Contraseña requerida"
            isValid = false
        }
        if (confirmPassword.isEmpty() || password != confirmPassword) {
            confirmPasswordInput.error = "Las contraseñas no coinciden"
            isValid = false
        }
        return isValid
    }
}