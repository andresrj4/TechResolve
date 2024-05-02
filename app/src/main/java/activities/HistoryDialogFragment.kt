package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.HistoryEntry
import model.Ticket

class HistoryDialogFragment : DialogFragment() {

    private lateinit var viewModel: TicketViewModel
    private lateinit var adapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(ticketId: String): HistoryDialogFragment =
            HistoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("ticketId", ticketId)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_dialog, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)

        recyclerView = view.findViewById(R.id.history_dialog_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = HistoryAdapter(mutableListOf())
        recyclerView.adapter = adapter
        arguments?.getString("ticketId")?.let { ticketId ->
            viewModel.loadHistoryForTicket(ticketId)
            viewModel.historyEntriesLiveData.observe(viewLifecycleOwner) { entries ->
                adapter.updateData(entries)
            }
        }
        view.findViewById<Button>(R.id.history_dialog_close_btn).setOnClickListener {
            dismiss()
        }
        return view
    }
}
