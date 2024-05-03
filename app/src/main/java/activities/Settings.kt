package activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.sistema_de_tickets.R

class Settings : Fragment() {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        etOldPassword = view.findViewById(R.id.settings_current_password)
        etNewPassword = view.findViewById(R.id.settings_new_password)
        etConfirmPassword = view.findViewById(R.id.settings_confirm_password)
        val btnChangePassword = view.findViewById<Button>(R.id.settings_change_password_btn)

        btnChangePassword.setOnClickListener {
            changePassword()
        }

        return view
    }

    private fun changePassword() {
        val oldPassword = etOldPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        UserManager().changePassword(oldPassword, newPassword, confirmPassword) { success, message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (success) {
                etOldPassword.text.clear()
                etNewPassword.text.clear()
                etConfirmPassword.text.clear()
            }
        }
    }
}