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
    private lateinit var ticketsAdapter: TicketsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TicketViewModel
    private var currentSort = TicketViewModel.SortBy.DATE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)
        recyclerView = view.findViewById(R.id.client_ticket_recycle_viewer)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter with an empty list before any LiveData setup
        ticketsAdapter = TicketsAdapter(mutableListOf()) { ticket ->
            navigateToTicketDetail(ticket)
        }
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

    private fun navigateToTicketDetail(ticket: Ticket) {
        // Implementation of navigation to the ticket detail fragment or activity
    }
}