package activities

import android.content.ContentValues.TAG
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import model.Ticket
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import model.HistoryEntry
import model.Material
import model.TicketStatus

class TicketViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val ticketsLiveData: MutableLiveData<List<Ticket>> = MutableLiveData()
    val selectedTicketLiveData = MutableLiveData<Ticket>()  // LiveData for specific ticket details
    var currentSortOrder = MutableLiveData(SortBy.DATE) // Default sort by date

    enum class SortBy {
        DATE,
        STATUS
    }

    fun loadOpenAndPendingTickets() {
        db.collection("Tickets")
            .whereEqualTo("ticketStatus", TicketStatus.OPEN.name)
            .get(Source.SERVER)
            .addOnSuccessListener { snapshots ->
                val ticketsList = ArrayList<Ticket>()
                for (doc in snapshots.documents) {
                    doc.toObject(Ticket::class.java)?.let {
                        ticketsList.add(it)
                    }
                }
                ticketsLiveData.postValue(ticketsList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Data fetch from server failed", e)
            }
    }

    fun submitTicket(title: String, description: String, userId: String) {
        val ticketRef = db.collection("Tickets").document()
        val currentTime = System.currentTimeMillis()
        val ticket = Ticket(
            ticketID = ticketRef.id,
            ticketTitle = title,
            ticketDescription = description,
            ticketDateCreated = currentTime,
            ticketClientID = userId,
            ticketStatus = TicketStatus.OPEN,
            ticketNotes = mutableListOf(),
            ticketMaterialsUsed = mutableListOf(),
            ticketHistory = mutableListOf(
                HistoryEntry(
                    timestamp = currentTime,
                    message = "Ticket created",
                    userId = userId
                )  // Ticket History Entry ^^
            )
        )
        ticketRef.set(ticket)
            .addOnSuccessListener { Log.d(TAG, "Ticket created successfully") }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to create ticket", e) }
    }

    fun loadClientTickets(clientId: String, sortBy: SortBy, source: Source = Source.CACHE) {
        val query = db.collection("Tickets")
            .whereEqualTo("ticketClientID", clientId)
            .orderBy(when (sortBy) {
                SortBy.DATE -> "ticketDateCreated"
                SortBy.STATUS -> "ticketStatus"
            }, Query.Direction.DESCENDING)
        query.get(source)
            .addOnSuccessListener { snapshots ->
                val ticketsList = ArrayList<Ticket>()
                for (doc in snapshots.documents) {
                    doc.toObject(Ticket::class.java)?.let {
                        ticketsList.add(it)
                    }
                }
                ticketsLiveData.postValue(ticketsList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to load tickets: ", e)
            }
    }

    fun setTicket(ticket: Ticket) {
        selectedTicketLiveData.value = ticket
    }

    fun claimTicket(ticket: Ticket) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not logged in")
            return
        }

        val ticketUpdates = mapOf(
            "ticketStatus" to TicketStatus.IN_PROGRESS.name,
            "ticketEmployeeID" to currentUser.uid
        )

        FirebaseFirestore.getInstance().collection("Tickets").document(ticket.ticketID)
            .update(ticketUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "Ticket claimed successfully.")
                val updatedTicket = ticket.copy(
                    ticketStatus = TicketStatus.IN_PROGRESS,
                    ticketEmployeeID = currentUser.uid
                )
                selectedTicketLiveData.postValue(updatedTicket)  // Update LiveData to refresh UI
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to claim ticket: ${e.message}")
            }
    }

    fun setupTicketListener() {
        db.collection("Tickets")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val ticketsList = ArrayList<Ticket>()
                for (doc in snapshots!!) {
                    doc.toObject(Ticket::class.java)?.let {
                        ticketsList.add(it)
                    }
                }
                ticketsLiveData.postValue(ticketsList)
            }
    }

    fun addMaterialToTicket(ticketId: String, material: Material) {
        db.collection("Tickets").document(ticketId)
            .update("ticketMaterialsUsed", FieldValue.arrayUnion(material))
            .addOnSuccessListener { Log.d(TAG, "Material added to ticket") }
            .addOnFailureListener { e -> Log.e(TAG, "Error adding material to ticket", e) }
    }
    /*
    private fun showHistoryDialog(ticket: Ticket) {
        // Similar to materials, use a DialogFragment to display history
        HistoryDialogFragment.newInstance(ticket.ticketHistory).show(parentFragmentManager, "HistoryDialog")
    }*/

    fun addNoteToTicket(ticketId: String, note: String) {
        val timestamp = System.currentTimeMillis()
        db.collection("Tickets").document(ticketId)
            .update(
                "ticketNotes", FieldValue.arrayUnion(note),
                "ticketHistory", FieldValue.arrayUnion(HistoryEntry(timestamp, note, FirebaseAuth.getInstance().currentUser?.uid ?: ""))
            )
            .addOnSuccessListener { Log.d(TAG, "Note added to ticket") }
            .addOnFailureListener { e -> Log.e(TAG, "Error adding note to ticket", e) }
    }



    fun updateTicketStatus(ticketId: String, newStatus: TicketStatus) {
        db.collection("Tickets").document(ticketId)
            .update("ticketStatus", newStatus.name)
            .addOnSuccessListener { Log.d(TAG, "Ticket status updated") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating ticket status", e) }
    }

    fun updateTicket(ticket: Ticket) {
        db.collection("Tickets").document(ticket.ticketID)
            .set(ticket, SetOptions.merge())
            .addOnSuccessListener { Log.d("TicketViewModel", "Ticket successfully updated!") }
            .addOnFailureListener { e -> Log.e("TicketViewModel", "Error updating ticket", e) }
        }

    fun addNoteToTicket(ticketId: String, note: String, userId: String) {
        val timestamp = System.currentTimeMillis()
        db.collection("Tickets").document(ticketId)
            .update("ticketNotes", FieldValue.arrayUnion(note),
                "ticketHistory", FieldValue.arrayUnion(HistoryEntry(timestamp, note, userId)))
            .addOnSuccessListener { Log.d("TicketViewModel", "Note added to ticket") }
            .addOnFailureListener { e -> Log.e("TicketViewModel", "Error adding note to ticket", e) }
        }
}