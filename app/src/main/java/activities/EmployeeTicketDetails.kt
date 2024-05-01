package activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.Material
import model.Ticket
import model.TicketStatus

class EmployeeTicketDetails : Fragment() {
    companion object {
        fun newInstance(ticket: Ticket): EmployeeTicketDetails {
            val args = Bundle()
            args.putSerializable("ticket", ticket)
            return EmployeeTicketDetails().apply { arguments = args }
        }
    }

    private lateinit var viewModel: TicketViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_employee_ticket_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        val ticket = arguments?.getSerializable("ticket") as Ticket?
        ticket?.let {
            viewModel.setTicket(it)
            updateUI(it)
            setupButtonActions(it)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        viewModel.selectedTicketLiveData.observe(viewLifecycleOwner) { ticket ->
            updateUI(ticket)
        }
    }

    private fun updateUI(ticket: Ticket) {
        val rootView = view ?: return
        rootView.setBackgroundResource(ticket.ticketStatus.getBackgroundResource())

        rootView.findViewById<TextView>(R.id.employee_ticket_details_ticketID).text = ticket.ticketID
        rootView.findViewById<TextView>(R.id.employee_ticket_details_ticket_status).apply {
            text = ticket.ticketStatus.getDisplayString()
            setTextColor(ContextCompat.getColor(requireContext(), ticket.ticketStatus.getColor()))
        }
        rootView.findViewById<TextView>(R.id.employee_ticket_details_ticket_title).text = ticket.ticketTitle
        rootView.findViewById<TextView>(R.id.employee_ticket_details_ticket_description).text = ticket.ticketDescription

        UserManager.getInstance().fetchAndDisplayUserName(ticket.ticketClientID) { name, lastName ->
            rootView.findViewById<TextView>(R.id.employee_ticket_details_client_assigned).text = "$name $lastName"
        }

        setupButtonActions(ticket)
    }

    private fun setupButtonActions(ticket: Ticket) {
        val rootView = view ?: return
        rootView.findViewById<Button>(R.id.employee_ticket_details_materials_btn).setOnClickListener {
            showMaterialsDialog(ticket)
        }

        rootView.findViewById<Button>(R.id.employee_ticket_details_history_btn).setOnClickListener {
            showHistoryDialog(ticket)
        }

        rootView.findViewById<Button>(R.id.employee_ticket_details_notes_btn).setOnClickListener {
            showNotesDialog(ticket)
        }

        rootView.findViewById<Button>(R.id.employee_ticket_change_status_btn).setOnClickListener {
            showChangeStatusDialog(ticket)
        }

        val claimButton = rootView.findViewById<Button>(R.id.employee_ticket_claim_btn)
        claimButton.visibility = if (ticket.ticketStatus in listOf(TicketStatus.OPEN, TicketStatus.PENDING) &&
            ticket.ticketEmployeeID == null) View.VISIBLE else View.GONE
        claimButton.setOnClickListener {
            viewModel.claimTicket(ticket)
        }
    }

    private fun showMaterialsDialog(ticket: Ticket) {
        MaterialDialogFragment.newInstance(ticket).show(parentFragmentManager, "MaterialDialog")
    }

    private fun showHistoryDialog(ticket: Ticket) {
        HistoryDialogFragment.newInstance(ticket.ticketID).show(parentFragmentManager, "HistoryDialog")
    }

    private fun showNotesDialog(ticket: Ticket) {
        NotesDialogFragment.newInstance(ticket).show(parentFragmentManager, "NotesDialog")
    }

    private fun showChangeStatusDialog(ticket: Ticket) {
        ChangeStatusDialogFragment.newInstance(ticket).show(parentFragmentManager, "ChangeStatusDialog")
    }
}