package activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import model.Ticket
import model.TicketStatus

class MyWork : Fragment() {
    private lateinit var availableTicketsAdapter: TicketAdapter
    private lateinit var assignedTicketsAdapter: TicketAdapter
    private lateinit var availableTicketsRecyclerView: RecyclerView
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
        availableTicketsRecyclerView = view.findViewById(R.id.employee_available_tickets_recycle_viewer)
        assignedTicketsRecyclerView = view.findViewById(R.id.employee_assigned_tickets_recycle_viewer)

        availableTicketsRecyclerView.layoutManager = LinearLayoutManager(context)
        assignedTicketsRecyclerView.layoutManager = LinearLayoutManager(context)

        val isEmployee = UserManager.getInstance().isEmployee()

        availableTicketsAdapter = TicketAdapter(mutableListOf(), isEmployee,
            onItemClick = { ticket ->
            },
            onDetailsClick = { ticket ->
                val isEmployee = UserManager.getInstance().isEmployee()
                Log.d("RoleCheck", "Is employee: $isEmployee")
                navigateToTicketDetail(ticket, isEmployee)
            }
        )
        assignedTicketsAdapter = TicketAdapter(mutableListOf(), isEmployee,
            onItemClick = { ticket ->
            },
            onDetailsClick = { ticket ->
                val isEmployee = UserManager.getInstance().isEmployee()
                Log.d("RoleCheck", "Is employee: $isEmployee")
                navigateToTicketDetail(ticket, isEmployee)
            }
        )

        availableTicketsRecyclerView.adapter = availableTicketsAdapter
        assignedTicketsRecyclerView.adapter = assignedTicketsAdapter
    }

    private fun setupClickListeners(view: View) {
        val openTicketsLabel = view.findViewById<TextView>(R.id.employee_available_tickets_sort_header)
        val assignedTicketsLabel = view.findViewById<TextView>(R.id.employee_assigned_tickets_sort_header)

        openTicketsLabel.setOnClickListener {
            toggleVisibility(availableTicketsRecyclerView)
        }
        assignedTicketsLabel.setOnClickListener {
            toggleVisibility(assignedTicketsRecyclerView)
        }
    }

    fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        viewModel.setupTicketListener()
        viewModel.ticketsLiveData.observe(viewLifecycleOwner) { tickets ->
            updateTicketLists(tickets)
        }
    }

    private fun updateTicketLists(tickets: List<Ticket>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        availableTicketsAdapter.updateData(tickets.filter {
            it.ticketStatus == TicketStatus.OPEN || it.ticketStatus == TicketStatus.PENDING
        }.toMutableList())

        assignedTicketsAdapter.updateData(tickets.filter {
            it.ticketEmployeeID == userId
        }.toMutableList())
    }

    private fun toggleVisibility(recyclerView: RecyclerView) {
        recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
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