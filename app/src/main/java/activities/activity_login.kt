package activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
                Toast.makeText(this, "Correo y contraseña estan vacios", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        val createAccountText = findViewById<TextView>(R.id.login_signup_link_text)
        createAccountText.setOnClickListener {
            // Start signup activity without finishing this one
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
                    Toast.makeText(baseContext, "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            val userId = user.uid
            val userRef = database.getReference("Users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChild("role") && snapshot.child("role").value != null) {
                        val role = snapshot.child("role").value.toString()
                        // Redirect to main activity with user role
                        startActivity(Intent(this@activity_login, activity_app::class.java))
                        finish()
                    } else {
                        // Prompt for role selection
                        promptForRoleSelection(userId)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(baseContext, "Failed to load user role.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Optionally clear input fields
            emailInput_login.text.clear()
            passwordInput_login.text.clear()
        }
    }

    private fun promptForRoleSelection(userId: String) {
        val dialogView = layoutInflater.inflate(R.layout.frontpage_role_selection, null)
        val builder = AlertDialog.Builder(this, R.style.DialogAnimationFadeIn)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()

        // Setup buttons and other interactive elements
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
            dialog.dismiss()  // Close the current dialog before showing it again
            promptForRoleSelection(userId)  // Optionally re-prompt if no selection is made
        }

        dialog.show()
    }


    private fun saveUserRole(userId: String, role: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users").child(userId)

        userRef.child("role").setValue(role).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Role set successfully, redirect to the main activity
                startActivity(Intent(this, activity_app::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to set role, try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}