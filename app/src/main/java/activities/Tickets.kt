package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import model.Ticket

class Tickets : Fragment() {
    private lateinit var ticketsAdapter: TicketAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TicketViewModel
    private var currentSort = TicketViewModel.SortBy.DATE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)
        recyclerView = view.findViewById(R.id.client_ticket_recycle_viewer)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val isEmployee = UserManager.getInstance().isEmployee()

        ticketsAdapter = TicketAdapter(mutableListOf(), isEmployee,
            onItemClick = { ticket ->
                // Handle general item click
            },
            onDetailsClick = { ticket ->
                navigateToTicketDetail(ticket, isEmployee) // Navigate to details using the details button specifically
            }
        )
        recyclerView.adapter = ticketsAdapter

        setupViewModel()
        return view
    }


    private fun toggleSortOrder() {
        currentSort = if (currentSort == TicketViewModel.SortBy.DATE) {
            TicketViewModel.SortBy.STATUS
        } else {
            TicketViewModel.SortBy.DATE
        }
        viewModel.loadClientTickets(FirebaseAuth.getInstance().currentUser?.uid ?: "", currentSort)
        updateSortHeader()
    }

    private fun updateSortHeader() {
        val headerView = view?.findViewById<TextView>(R.id.client_sort_header)
        headerView?.text = if (currentSort == TicketViewModel.SortBy.DATE) "Sort by Date" else "Sort by Status"
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel.loadClientTickets(userId, currentSort, Source.SERVER)  // Fetch from server to ensure data is fresh
        viewModel.ticketsLiveData.observe(viewLifecycleOwner) { tickets ->
            val mutableTickets = tickets.toMutableList()
            ticketsAdapter.updateData(mutableTickets)  // Update the data in the existing adapter
            if (tickets.isEmpty()) {
                Toast.makeText(context, "No tickets available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToTicketDetail(ticket: Ticket, isEmployee: Boolean) {
        val fragment = if (isEmployee) {
            EmployeeTicketDetails.newInstance(ticket)
        } else {
            ClientTicketDetails.newInstance(ticket)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}