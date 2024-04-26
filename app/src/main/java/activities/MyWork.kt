package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import model.Ticket
import model.TicketStatus

class MyWork : Fragment() {
    private lateinit var openTicketsAdapter: TicketsAdapter
    private lateinit var assignedTicketsAdapter: TicketsAdapter
    private lateinit var openTicketsRecyclerView: RecyclerView
    private lateinit var assignedTicketsRecyclerView: RecyclerView
    private lateinit var viewModel: TicketViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_work, container, false)

        setupRecyclerViews(view)
        setupClickListeners(view)
        setupViewModel()

        return view
    }

    private fun setupRecyclerViews(view: View) {
        openTicketsRecyclerView = view.findViewById(R.id.employee_open_tickets_recycle_viewer)
        assignedTicketsRecyclerView = view.findViewById(R.id.employee_assigned_tickets_recycle_viewer)

        openTicketsRecyclerView.layoutManager = LinearLayoutManager(context)
        assignedTicketsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Set initial adapters with mutable empty lists
        openTicketsAdapter = TicketsAdapter(mutableListOf()) { ticket -> claimTicket(ticket) }
        assignedTicketsAdapter = TicketsAdapter(mutableListOf()) { ticket -> navigateToTicketDetail(ticket) }

        openTicketsRecyclerView.adapter = openTicketsAdapter
        assignedTicketsRecyclerView.adapter = assignedTicketsAdapter
    }

    private fun setupClickListeners(view: View) {
        val openTicketsLabel = view.findViewById<TextView>(R.id.employee_open_tickets_sort_header)
        val assignedTicketsLabel = view.findViewById<TextView>(R.id.employee_in_process_tickets_sort_header)

        openTicketsLabel.setOnClickListener {
            toggleVisibility(openTicketsRecyclerView)
        }
        assignedTicketsLabel.setOnClickListener {
            toggleVisibility(assignedTicketsRecyclerView)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        viewModel.loadOpenTickets()
        viewModel.ticketsLiveData.observe(viewLifecycleOwner) { tickets ->
            openTicketsAdapter.updateData(tickets.filter { it.ticketStatus == TicketStatus.OPEN }.toMutableList())
            openTicketsRecyclerView.visibility = if (tickets.isNotEmpty()) View.VISIBLE else View.GONE
            if (tickets.isEmpty()) {
                Toast.makeText(context, "No open tickets available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleVisibility(recyclerView: RecyclerView) {
        recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun claimTicket(ticket: Ticket) {
        // Implementation to claim an open ticket
    }

    private fun navigateToTicketDetail(ticket: Ticket) {
        // Navigate to detail view with options to update ticket status or add notes/materials
    }
}