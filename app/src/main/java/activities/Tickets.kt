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
    private var currentSort = TicketViewModel.SortBy.DATE_CREATED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)
        recyclerView = view.findViewById(R.id.client_ticket_recycle_viewer)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val isEmployee = UserManager.getInstance().isEmployee()

        ticketsAdapter = TicketAdapter(mutableListOf(), isEmployee,
            onItemClick = { ticket ->
            },
            onDetailsClick = { ticket ->
                navigateToTicketDetail(ticket, isEmployee)
            }
        )

        recyclerView.adapter = ticketsAdapter
        setupViewModel()

        val sortHeader = view.findViewById<TextView>(R.id.client_sort_header)
        sortHeader.setOnClickListener {
            toggleSortOrder()
        }

        return view
    }

    private fun toggleSortOrder() {
        currentSort = when (currentSort) {
            TicketViewModel.SortBy.DATE_CREATED -> TicketViewModel.SortBy.LAST_UPDATED
            TicketViewModel.SortBy.LAST_UPDATED -> TicketViewModel.SortBy.STATUS
            TicketViewModel.SortBy.STATUS -> TicketViewModel.SortBy.DATE_CREATED
        }
        viewModel.loadClientTickets(FirebaseAuth.getInstance().currentUser?.uid ?: "", currentSort)
        updateSortHeader()
    }

    private fun updateSortHeader() {
        val headerView = view?.findViewById<TextView>(R.id.client_sort_header)
        headerView?.text = when (currentSort) {
            TicketViewModel.SortBy.DATE_CREATED -> "Fecha"
            TicketViewModel.SortBy.LAST_UPDATED -> "Ultima actualización"
            TicketViewModel.SortBy.STATUS -> "Estado"
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel.loadClientTickets(userId, currentSort, Source.SERVER)
        viewModel.ticketsLiveData.observe(viewLifecycleOwner) { tickets ->
            val mutableTickets = tickets.toMutableList()
            ticketsAdapter.updateData(mutableTickets)
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