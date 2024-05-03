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

class ActivityLogin : AppCompatActivity() {
    private lateinit var emailInput_login: EditText
    private lateinit var passwordInput_login: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        emailInput_login = findViewById(R.id.emailinput_login)
        passwordInput_login = findViewById(R.id.passwordinput_login)
        loginBtn = findViewById(R.id.login_btn)

        findViewById<View>(R.id.login_content).visibility = View.VISIBLE
        findViewById<View>(R.id.signup_content).visibility = View.GONE

        loginBtn.setOnClickListener {
            val email = emailInput_login.text.toString().trim()
            val password = passwordInput_login.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                UserManager.getInstance().loginUser(email, password, {
                    startActivity(Intent(this, ActivityApp::class.java))
                    finish()
                }, { errorMsg ->
                    Toast.makeText(this, "Login failed: $errorMsg", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this, "Correo y contraseña están vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.login_signup_link_text).setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
    }
}