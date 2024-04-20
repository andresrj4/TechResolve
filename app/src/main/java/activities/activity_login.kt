package activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R

class activity_login : AppCompatActivity() {
    private lateinit var loginContent: View
    private lateinit var signupContent: View

    lateinit var emailInput : EditText
    lateinit var passwordInput : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        loginContent = findViewById(R.id.login_content)
        signupContent = findViewById(R.id.signup_content)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        loginContent.visibility = View.VISIBLE
        signupContent.visibility = View.GONE

        loginBtn.setOnClickListener {
            val username = emailInput.text.toString()
            val password = passwordInput.text.toString()
            Log.i("Credentials","Email : $username and Password : $password")

            val intent = Intent(this, activity_app::class.java)
            startActivity(intent)
        }

        val createAccountText = findViewById<TextView>(R.id.signup_login_link)
        createAccountText.setOnClickListener {
            val intent = Intent(this, activity_signup::class.java)
            startActivity(intent)
        }
    }
}