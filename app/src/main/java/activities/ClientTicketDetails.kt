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
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticket_title).text =
            ticket.ticketTitle
        rootView.findViewById<TextView>(R.id.client_ticket_details_ticket_description).text =
            ticket.ticketDescription

        UserManager.getInstance()
            .fetchAndDisplayUserName(ticket.ticketEmployeeID ?: "") { name, lastName ->
                rootView.findViewById<TextView>(R.id.client_ticket_details_employee_assigned).text =
                    "$name $lastName"
            }

        rootView.findViewById<Button>(R.id.client_ticket_details_materials_btn).visibility =
            if ((ticket.ticketStatus != TicketStatus.OPEN) && (ticket.ticketMaterialsUsed.isNotEmpty() || (ticket.ticketStatus in listOf(
                    TicketStatus.RESOLVED,
                    TicketStatus.CLOSED
                ) && ticket.ticketMaterialsUsed.isEmpty()))
            ) View.VISIBLE else View.GONE

        rootView.findViewById<Button>(R.id.client_ticket_details_history_btn).visibility =
            if (ticket.ticketStatus != TicketStatus.OPEN) View.VISIBLE else View.GONE

        rootView.findViewById<Button>(R.id.client_ticket_details_close_ticket_btn).visibility =
            if (ticket.ticketStatus in listOf(
                    TicketStatus.CLOSED,
                    TicketStatus.RESOLVED
                )
            ) View.GONE else View.VISIBLE
    }
}
