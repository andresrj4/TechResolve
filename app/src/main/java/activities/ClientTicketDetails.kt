package activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import model.Ticket
import model.TicketStatus

class ClientTicketDetails : Fragment() {
    companion object {
        fun newInstance(ticket: Ticket): ClientTicketDetails {
            val args = Bundle()
            args.putSerializable("ticket", ticket)
            return ClientTicketDetails().apply { arguments = args }
        }
    }

    private lateinit var viewModel: TicketViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_ticket_details, container, false)
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
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticketID).text = ticket.ticketID
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticket_status).apply {
            text = ticket.ticketStatus.getDisplayString()
            setTextColor(ContextCompat.getColor(requireContext(), ticket.ticketStatus.getColor()))
        }
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticket_title).text = ticket.ticketTitle
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticket_description).text = ticket.ticketDescription

        val employeeId = ticket.ticketEmployeeID
        if (!employeeId.isNullOrEmpty()) {
            UserManager.getInstance().fetchAndDisplayUserName(employeeId) { name, lastName ->
                rootView.findViewById<TextView>(R.id.client_ticket_details_employee_assigned).text = "Empleado: $name $lastName"
            }
        } else {
            rootView.findViewById<TextView>(R.id.client_ticket_details_employee_assigned).text = "Empleado: No asignado"
        }
        updateVisibilityBasedOnStatus(ticket)
    }

    private fun updateVisibilityBasedOnStatus(ticket: Ticket) {
        val rootView = view ?: return

        val materialsButton = rootView.findViewById<Button>(R.id.client_ticket_details_materials_btn)
        val historyButton = rootView.findViewById<Button>(R.id.client_ticket_details_history_btn)
        val closeTicketButton = rootView.findViewById<Button>(R.id.client_ticket_details_close_ticket_btn)

        materialsButton.visibility = if (ticket.ticketStatus == TicketStatus.OPEN) View.GONE else View.VISIBLE
        historyButton.visibility = if (ticket.ticketStatus == TicketStatus.OPEN) View.GONE else View.VISIBLE

        closeTicketButton.visibility = if (ticket.ticketStatus == TicketStatus.RESOLVED || ticket.ticketStatus == TicketStatus.CLOSED) View.GONE else View.VISIBLE
    }

    private fun setupButtonActions(ticket: Ticket) {
        val rootView = view ?: return
        rootView.findViewById<Button>(R.id.client_ticket_details_materials_btn).setOnClickListener {
            showMaterialsDialog(ticket)
        }

        rootView.findViewById<Button>(R.id.client_ticket_details_history_btn).setOnClickListener {
            showHistoryDialog(ticket)
        }

        rootView.findViewById<Button>(R.id.client_ticket_details_close_ticket_btn).setOnClickListener {
            showConfirmationDialog(ticket)
        }
    }

    private fun showMaterialsDialog(ticket: Ticket) {
        MaterialDialogFragment.newInstance(ticket).show(parentFragmentManager, "MaterialDialog")
    }

    private fun showHistoryDialog(ticket: Ticket) {
        HistoryDialogFragment.newInstance(ticket.ticketID).show(parentFragmentManager, "HistoryDialog")
    }

    private fun showConfirmationDialog(ticket: Ticket) {
        ConfirmationDialog.newInstance(
            title = "Cerrar Ticket",
            message = "¿Está seguro de que desea cerrar este ticket?",
            positiveButtonText = "Sí",
            negativeButtonText = "No",
            onConfirm = { closeTicket(ticket) },
            onCancel = {  }
        ).show(parentFragmentManager, "ConfirmationDialog")
    }

    private fun closeTicket(ticket: Ticket) {
        viewModel.lockTicket(ticket.ticketID)
        viewModel.updateTicketStatus(ticket.ticketID, TicketStatus.CLOSED, "Ticket closed by client")
        ticket.ticketEmployeeID?.let { employeeId ->
            viewModel.addNotificationForUser(employeeId, "Uno de tus tickets asignados fue cancelado.")
        }
    }
}