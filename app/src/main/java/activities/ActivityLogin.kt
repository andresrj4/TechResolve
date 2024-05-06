package activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                UserManager.getInstance().loginUser(email, password, this::updateUI) { errorMsg ->
                    Toast.makeText(this, "Login failed: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Correo y contraseña están vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.login_signup_link_text).setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
    }

    private fun updateUI(user: FirebaseUser) {
        val userId = user.uid
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChild("role") && snapshot.child("role").value != null) {
                    startActivity(Intent(this@ActivityLogin, ActivityApp::class.java))
                    finish()
                } else {
                    promptForRoleSelection(userId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ActivityLogin, "Failed to load user role.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun promptForRoleSelection(userId: String) {
        val dialogView = layoutInflater.inflate(R.layout.frontpage_role_selection, null)
        val builder = AlertDialog.Builder(this, R.style.DialogAnimationFadeIn)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.client_btn).setOnClickListener {
            saveUserRole(userId, "Cliente")
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.employee_btn).setOnClickListener {
            saveUserRole(userId, "Empleado")
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            Toast.makeText(this, "You must select a role to continue.", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            promptForRoleSelection(userId)
        }

        dialog.show()
    }

    private fun saveUserRole(userId: String, role: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users").child(userId)

        userRef.child("role").setValue(role).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                UserManager.getInstance().currentUserRole = role
                startActivity(Intent(this, ActivityApp::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to set role, try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}