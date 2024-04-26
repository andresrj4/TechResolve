package activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.sistema_de_tickets.R
import model.Ticket

class EmployeeTicketDetails : Fragment() {
    private lateinit var viewModel: TicketViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_employee_ticket_details, container, false)
        setupViewModel()
        return view
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(TicketViewModel::class.java)
        viewModel.selectedTicketLiveData.observe(viewLifecycleOwner, { ticket ->
            updateUI(ticket)
        })
    }

    private fun updateUI(ticket: Ticket) {
        // Update your views here
        // Plus additional controls for employees
    }
}
