package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Material
import model.Ticket

class MaterialDialogFragment : DialogFragment() {

    private lateinit var adapter: MaterialsAdapter
    private lateinit var materialsList: RecyclerView
    private lateinit var materialNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var ticket: Ticket
    private lateinit var viewModel: TicketViewModel

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

        // Initialize the materials adapter based on the ticket's existing materials or with an empty list.
        arguments?.getSerializable("ticket")?.let {
            ticket = it as Ticket
            adapter = MaterialsAdapter(ticket.ticketMaterialsUsed.toMutableList()) // Initialize with current materials
        } ?: run {
            adapter = MaterialsAdapter(mutableListOf()) // Fallback to empty list if no ticket is found
        }
        materialsList = view.findViewById(R.id.material_dialog_material_list_recycler_view)
        materialsList.adapter = adapter
        materialsList.layoutManager = LinearLayoutManager(context)

        materialNameEditText = view.findViewById(R.id.material_dialog_material_name_edittext)
        quantityEditText = view.findViewById(R.id.material_dialog_quantity_edittext)
        priceEditText = view.findViewById(R.id.material_dialog_price_edittext)

        view.findViewById<Button>(R.id.material_dialog_add_material_btn).setOnClickListener {
            addMaterial()
        }
        view.findViewById<Button>(R.id.material_dialog_close_btn).setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun addMaterial() {
        val name = materialNameEditText.text.toString()
        val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
        val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (name.isNotEmpty() && quantity > 0 && price > 0.0) {
            val material = Material(name, quantity, price)
            adapter.addMaterial(material)
            viewModel.addMaterialToTicket(ticket.ticketID, material) // Update the ticket
            materialNameEditText.text.clear()
            quantityEditText.text.clear()
            priceEditText.text.clear()
        } else {
            Toast.makeText(context, "Please enter valid values for all fields", Toast.LENGTH_SHORT).show()
        }
    }
}