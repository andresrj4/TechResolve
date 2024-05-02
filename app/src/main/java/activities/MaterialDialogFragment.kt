package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Material
import model.Ticket
import model.TicketStatus

class MaterialDialogFragment : DialogFragment() {

    private lateinit var adapter: MaterialsAdapter
    private lateinit var materialsList: RecyclerView
    private lateinit var materialNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var materialNameTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var addMaterialButton: Button
    private lateinit var ticket: Ticket
    private lateinit var viewModel: TicketViewModel
    private lateinit var grandTotalTextView: TextView

    companion object {
        fun newInstance(ticket: Ticket): MaterialDialogFragment =
            MaterialDialogFragment().apply {
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_material_dialog, container, false)
        arguments?.getSerializable("ticket")?.let {
            ticket = it as Ticket
            adapter = MaterialsAdapter(ticket.ticketMaterialsUsed.toMutableList())
        } ?: run {
            adapter = MaterialsAdapter(mutableListOf())
        }
        setupViews(view)
        setupLiveDataObservers(view)
        return view
    }

    private fun setupViews(view: View) {
        grandTotalTextView = view.findViewById(R.id.material_dialog_grand_total_textview)
        materialNameTextView = view.findViewById(R.id.material_dialog_material_name_textview)
        materialNameEditText = view.findViewById(R.id.material_dialog_material_name_edittext)
        quantityTextView = view.findViewById(R.id.material_dialog_quantity_textview)
        quantityEditText = view.findViewById(R.id.material_dialog_quantity_edittext)
        priceTextView = view.findViewById(R.id.material_dialog_price_textview)
        priceEditText = view.findViewById(R.id.material_dialog_price_edittext)
        addMaterialButton = view.findViewById(R.id.material_dialog_add_material_btn)
        materialsList = view.findViewById(R.id.material_dialog_material_list_recycler_view)
        materialsList.adapter = adapter
        materialsList.layoutManager = LinearLayoutManager(context)
        view.findViewById<Button>(R.id.material_dialog_add_material_btn).setOnClickListener {
            addMaterial()
        }
        view.findViewById<Button>(R.id.material_dialog_close_btn).setOnClickListener {
            dismiss()
        }
    }

    private fun setupLiveDataObservers(view: View) {
        viewModel.selectedTicketLiveData.observe(viewLifecycleOwner) { updatedTicket ->
            ticket = updatedTicket
            val isEmployee = UserManager.getInstance().isEmployee()
            updateMaterialEntryVisibility(view, ticket.ticketStatus, isEmployee)
            updateGrandTotalVisibility()
        }
    }

    private fun updateMaterialEntryVisibility(view: View, status: TicketStatus, isEmployee: Boolean) {
        val canEdit = isEmployee && status in listOf(TicketStatus.OPEN, TicketStatus.IN_PROGRESS)
        val visibility = if (canEdit) View.VISIBLE else View.GONE
        materialNameTextView.visibility = visibility
        materialNameEditText.visibility = visibility
        quantityTextView.visibility = visibility
        quantityEditText.visibility = visibility
        priceTextView.visibility = visibility
        priceEditText.visibility = visibility
        addMaterialButton.visibility = visibility
    }

    private fun updateGrandTotalVisibility() {
        if (ticket.ticketStatus == TicketStatus.RESOLVED) {
            grandTotalTextView.visibility = View.VISIBLE
            val totalSum = ticket.ticketMaterialsUsed.sumOf { it.total }
            grandTotalTextView.text = String.format("%.2f", totalSum)
        } else {
            grandTotalTextView.visibility = View.GONE
        }
    }

    private fun addMaterial() {
        val name = materialNameEditText.text.toString()
        val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
        val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (name.isNotEmpty() && quantity > 0 && price > 0.0) {
            val material = Material(name, quantity, price)
            adapter.addMaterial(material)
            viewModel.addMaterialToTicket(ticket.ticketID, material)
            materialNameEditText.text.clear()
            quantityEditText.text.clear()
            priceEditText.text.clear()
        } else {
            Toast.makeText(context, "Please enter valid values for all fields", Toast.LENGTH_SHORT).show()
        }
    }
}