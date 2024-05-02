package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Ticket
import model.TicketStatus

class NotesDialogFragment : DialogFragment() {
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var notesList: RecyclerView
    private lateinit var noteInputText: EditText
    private lateinit var viewModel: TicketViewModel
    private lateinit var ticket: Ticket

    companion object {
        fun newInstance(ticket: Ticket): NotesDialogFragment =
            NotesDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("ticket", ticket)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        arguments?.getSerializable("ticket")?.let { serializable ->
            ticket = serializable as Ticket
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notes_dialog, container, false)
        notesList = view.findViewById(R.id.notes_dialog_recycler_view)
        notesList.layoutManager = LinearLayoutManager(context)
        noteInputText = view.findViewById(R.id.note_input_text)
        arguments?.getSerializable("ticket")?.let {
            ticket = it as Ticket
            notesAdapter = NotesAdapter(ticket.ticketNotes.toMutableList())
        } ?: run {
            notesAdapter = NotesAdapter(mutableListOf())
        }
        notesList.adapter = notesAdapter
        viewModel.selectedTicketLiveData.observe(viewLifecycleOwner) { updatedTicket ->
            updatedTicket?.let {
                notesAdapter.updateData(it.ticketNotes)

                when (it.ticketStatus) {
                    TicketStatus.RESOLVED, TicketStatus.PENDING -> {
                        noteInputText.visibility = View.GONE
                        view.findViewById<Button>(R.id.add_note_button).visibility = View.GONE
                    }
                    else -> {
                        noteInputText.visibility = View.VISIBLE
                        view.findViewById<Button>(R.id.add_note_button).visibility = View.VISIBLE
                    }
                }
            }
        }

        view.findViewById<Button>(R.id.add_note_button).setOnClickListener {
            addNote()
        }
        view.findViewById<Button>(R.id.close_notes_dialog_button).setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun addNote() {
        val noteText = noteInputText.text.toString().trim()
        if (noteText.isNotEmpty()) {
            viewModel.addNoteToTicket(ticket.ticketID, noteText)
            noteInputText.text.clear()
        }
    }
}