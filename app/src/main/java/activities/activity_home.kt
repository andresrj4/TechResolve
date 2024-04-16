package activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sistema_de_tickets.R
import com.example.sistema_de_tickets.databinding.AppHomeBinding


class activity_home : AppCompatActivity(){

    private lateinit var binding : AppHomeBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = AppHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navbarPanel.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.navbar_tickets -> {
                    replaceFragment(Tickets())
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
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}