package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.sistema_de_tickets.R
import model.Ticket
import model.TicketStatus

class ChangeStatusDialogFragment : DialogFragment() {
    private lateinit var viewModel: TicketViewModel
    private var ticket: Ticket? = null

    companion object {
        fun newInstance(ticket: Ticket): ChangeStatusDialogFragment = ChangeStatusDialogFragment().apply {
            this.ticket = ticket
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_change_status_dialog, container, false)
        val noteInput = view.findViewById<EditText>(R.id.change_status_dialog_note_input)
        val changeToPendingButton = view.findViewById<Button>(R.id.change_status_dialog_to_pending_btn)
        val changeToResolvedButton = view.findViewById<Button>(R.id.change_status_dialog_to_resolved_btn)

        // Display EditText for notes immediately for both buttons
        noteInput.visibility = View.VISIBLE

        changeToPendingButton.setOnClickListener {
            if (noteInput.text.isNotEmpty()) {
                updateStatus(TicketStatus.PENDING, noteInput.text.toString())
            } else {
                noteInput.error = "Nota es obligatoria para cambiar a 'Pendiente'"
            }
        }

        changeToResolvedButton.setOnClickListener {
            if (noteInput.text.isNotEmpty()) {
                updateStatus(TicketStatus.RESOLVED, noteInput.text.toString())
            } else {
                noteInput.error = "Nota es obligatoria para cambiar a 'Resuelto'"
            }
        }

        view.findViewById<Button>(R.id.change_status_dialog_close_btn).setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun updateStatus(newStatus: TicketStatus, note: String) {
        ticket?.let {
            viewModel.updateTicketStatus(it.ticketID, newStatus, note)
            if (newStatus == TicketStatus.PENDING) {
                viewModel.unassignTicket(it.ticketID)
            }
            dismiss()
        }
    }
}