package activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sistema_de_tickets.databinding.FragmentTicketsBinding
import com.google.firebase.firestore.FirebaseFirestore

data class Ticket(
    val title: String = "",
    val description: String = "",
    val dateCreated: Long = 0L,
    var dateClosed: Long? = null,
    val clientID: String = "",
    val resolutionSteps: MutableList<String> = mutableListOf(),
    var status: TicketStatus = TicketStatus.OPEN,
    val materialsUsed: MutableList<String> = mutableListOf(),
    var note: String = "",
    val history: MutableList<String> = mutableListOf()
)

enum class TicketStatus {
    OPEN,
    CLOSED,
    PENDING,
    IN_PROGRESS
}

class Tickets : Fragment() {
    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btn_Add_Ticket.setOnClickListener {
            createTicket("Sample Ticket", "This is a sample description.")
        }
        loadTickets()
    }

    private fun loadTickets() {
        db.collection("tickets").get()
            .addOnSuccessListener { result ->
                val ticketsList = ArrayList<Ticket>()
                for (document in result) {
                    try {
                        val ticket = document.toObject(Ticket::class.java)
                        ticketsList.add(ticket)
                    } catch (e: Exception) {
                        Log.e("TicketsFragment", "Error converting document", e)
                    }
                }
                // Aquí podrías actualizar tu RecyclerView o cualquier otro elemento de la UI con ticketsList
            }
            .addOnFailureListener { exception ->
                Log.w("TicketsFragment", "Error getting documents: ", exception)
            }
    }


    private fun createTicket(title: String, description: String) {
        val newTicket = Ticket(
            title = title,
            description = description,
            dateCreated = System.currentTimeMillis(),
            clientID = "someClientId"
        )

        db.collection("tickets").add(newTicket)
            .addOnSuccessListener {
                Log.d("TicketsFragment", "Ticket added with ID: ${it.id}")
                // Refresh the list of tickets or show success message
            }
            .addOnFailureListener { e ->
                Log.w("TicketsFragment", "Error adding ticket", e)
                // Show error message to the user
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
