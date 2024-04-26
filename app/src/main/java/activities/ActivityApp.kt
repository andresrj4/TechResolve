package activities

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sistema_de_tickets.R
import androidx.appcompat.widget.Toolbar
import com.example.sistema_de_tickets.databinding.AppMainBinding
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivityApp : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var binding: AppMainBinding
    private lateinit var dotsIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var addTicketBtn : Button
    private lateinit var viewModel: TicketViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TicketViewModel::class.java)
        binding = AppMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)
        addTicketBtn = findViewById(R.id.btn_add_ticket)
        toolbar = findViewById(R.id.main_toolbar)
        dotsIcon = findViewById(R.id.toolbar_dotIcon)
        menuIcon = findViewById(R.id.toolbar_menuIcon)
        setSupportActionBar(toolbar)

        updateUserName()
        initializeUI()
        fetchUserRoleAndSetupNavigation()

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        val navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        ).apply {
            isDrawerIndicatorEnabled = false  // Disable the default icon
            syncState()
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        addTicketBtn.setOnClickListener {
            showTicketCreationForm()
        }

        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Home())
                .commit()
        }

        binding.navbarPanel.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.navbar_tickets, R.id.navbar_work -> {
                    val fragment = if (item.itemId == R.id.navbar_tickets) Tickets() else MyWork()
                    replaceFragment(fragment)
                    true
                }
                R.id.navbar_notifications -> {
                    replaceFragment(Notifications())
                    true
                }
                R.id.navbar_maps -> {
                    replaceFragment(Maps())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("NavigationItem", "Clicked item: ${item.title}")
        drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.toolbar_menu_settings -> {
                replaceFragment(Settings())
                return true
            }
            R.id.toolbar_menu_logout -> {
                val intent = Intent(this, ActivityLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears the activity stack
                startActivity(intent)
                Toast.makeText(this, "Cierre de sesión exitoso", Toast.LENGTH_SHORT).show()
                finish()  // Ensure the current activity is closed
                return true
            }
        }
        return true
    }

    private fun initializeUI() {
        setSupportActionBar(toolbar)
    }
    private fun setupNavigation(role: String?) {
        binding.navbarPanel.menu.apply {
            findItem(R.id.navbar_tickets).isVisible = role == "Cliente"
            findItem(R.id.navbar_work).isVisible = role == "Empleado"
        }
    }

    override fun onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchUserRoleAndSetupNavigation() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("Users").child(it)

            userRef.child("role").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.value as String?
                    setupNavigation(role)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to fetch user role", error.toException())
                }
            })
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.d("FragmentTransaction", "Replacing fragment with ${fragment::class.java.simpleName}")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun updateUserName() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("Users").child(userId)

            // Attach a listener to read the data at our user reference
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()  // Getting the name
                    val lastName = snapshot.child("lastName").value.toString()  // Getting the last name

                    // Update the TextView, combining name and last name
                    val userNameTextView = findViewById<TextView>(R.id.toolbar_username)
                    userNameTextView.text = "$name $lastName"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
                    Toast.makeText(baseContext, "Failed to load user.",
                        Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showTicketCreationForm() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.app_open_ticket)

        // Correctly find EditText by ID and cast them properly
        val ticketTitleEditText = dialog.findViewById<EditText>(R.id.ticket_title_editbox)
        val ticketDescriptionEditText = dialog.findViewById<EditText>(R.id.ticket_desc_editbox)
        val sendTicketBtn = dialog.findViewById<AppCompatButton>(R.id.submit_ticket_btn)

        val viewModel = ViewModelProvider(this).get(TicketViewModel::class.java)

        sendTicketBtn.setOnClickListener {
            val title = ticketTitleEditText.text.toString().trim()
            val description = ticketDescriptionEditText.text.toString().trim()
            if (title.isNotEmpty() && description.isNotEmpty()) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                viewModel.submitTicket(title, description, userId)
                dialog.dismiss()
                Toast.makeText(this, "Ticket enviado exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos antes de enviar.", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimationBottomToCenter
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}