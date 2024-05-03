package activities

import android.content.ContentValues.TAG
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import model.Ticket
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import model.HistoryEntry
import model.Material
import model.Notification
import model.TicketStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val ticketsLiveData: MutableLiveData<List<Ticket>> = MutableLiveData()
    val selectedTicketLiveData = MutableLiveData<Ticket>()
    val historyEntriesLiveData = MutableLiveData<List<HistoryEntry>>()
    val notificationLiveData: MutableLiveData<List<Notification>> = MutableLiveData()
    private fun getCurrentUserName(): String = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"

    enum class SortBy {
        DATE,
        STATUS
    }

    fun setTicket(ticket: Ticket) {
        selectedTicketLiveData.value = ticket
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

    private fun addHistoryEntry(ticketId: String, action: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
        val formattedDate = dateFormat.format(Date(timestamp))
        val message = "$formattedDate: $action"
        val historyEntry = HistoryEntry(timestamp, message, userId)
        db.collection("Tickets").document(ticketId)
            .update("ticketHistory", FieldValue.arrayUnion(historyEntry))
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add history entry: ", e)
            }
    }

    fun fetchNotificationsForUser(userId: String) {
        db.collection("Users").document(userId)
            .collection("Notifications")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { it.toObject(Notification::class.java) }
                notificationLiveData.postValue(notifications ?: listOf())
            }
    }

    fun markNotificationAsRead(userId: String, notificationId: String) {
        FirebaseFirestore.getInstance().collection("Users").document(userId)
            .collection("Notifications").document(notificationId)
            .update("isRead", true)
        FirebaseFirestore.getInstance().collection("Users").document(userId)
            .collection("Notifications").document(notificationId)
            .update("read", true)
            .addOnSuccessListener { Log.d(TAG, "Notification marked as read.") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to mark notification as read", e) }
    }

    fun addNotificationForUser(userId: String, message: String) {
        val notificationRef = FirebaseFirestore.getInstance().collection("Users").document(userId)
            .collection("Notifications").document()
        val newNotification = Notification(
            id = notificationRef.id,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        notificationRef.set(newNotification)
            .addOnSuccessListener {
                Log.d(TAG, "Notification added successfully with ID: ${notificationRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding notification", e)
            }
    }

    fun loadHistoryForTicket(ticketId: String) {
        db.collection("Tickets").document(ticketId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            snapshot?.toObject(Ticket::class.java)?.let {
                historyEntriesLiveData.postValue(it.ticketHistory)
            }
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
            ticketHistory = mutableListOf()
        )
        ticketRef.set(ticket)
            .addOnSuccessListener {
                Log.d(TAG, "Ticket created successfully")
                addHistoryEntry(ticket.ticketID, "Creación de ticket.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to create ticket", e)
            }
    }

    fun claimTicket(ticket: Ticket) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val updates = mapOf(
                "ticketStatus" to TicketStatus.IN_PROGRESS.name,
                "ticketEmployeeID" to currentUser.uid
            )
            db.collection("Tickets").document(ticket.ticketID)
                .update(updates)
                .addOnSuccessListener {
                    val updatedTicket = ticket.copy(
                        ticketStatus = TicketStatus.IN_PROGRESS,
                        ticketEmployeeID = currentUser.uid
                    )
                    selectedTicketLiveData.postValue(updatedTicket)
                    addHistoryEntry(ticket.ticketID, "Ticket fue asignado.")
                    if (ticket.ticketClientID != currentUser.uid) {
                        addNotificationForUser(ticket.ticketClientID, "Un empleado ha tomado el ticket: '${ticket.ticketTitle}'")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to claim ticket: ", e)
                }
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

    fun addMaterialToTicket(ticketId: String, material: Material) {
        db.collection("Tickets").document(ticketId)
            .update("ticketMaterialsUsed", FieldValue.arrayUnion(material))
            .addOnSuccessListener {
                addHistoryEntry(ticketId, "Material añadido: ${material.name}, Cantidad: ${material.quantity}, Precio: ${material.price}.")
                db.collection("Tickets").document(ticketId).get().addOnSuccessListener { document ->
                    val ticket = document.toObject(Ticket::class.java)
                    ticket?.let {
                        if (it.ticketClientID != FirebaseAuth.getInstance().currentUser?.uid) {
                            addNotificationForUser(it.ticketClientID, "Material nuevo fue usado en el ticket: '${it.ticketTitle}'.")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding material to ticket: ", e)
            }
    }

    fun addNoteToTicket(ticketId: String, noteText: String) {
        db.collection("Tickets").document(ticketId)
            .update("ticketNotes", FieldValue.arrayUnion(noteText))
            .addOnSuccessListener {
                selectedTicketLiveData.value?.let { currentTicket ->
                    val updatedNotes = currentTicket.ticketNotes.toMutableList()
                    updatedNotes.add(noteText)
                    val updatedTicket = currentTicket.copy(ticketNotes = updatedNotes)
                    selectedTicketLiveData.postValue(updatedTicket)
                    addHistoryEntry(ticketId, "Nueva nota: '$noteText'")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding note to ticket: ", e)
            }
    }

    fun updateTicketStatus(ticketId: String, newStatus: TicketStatus, note: String) {
        val updates = hashMapOf<String, Any>(
            "ticketStatus" to newStatus.name,
            "ticketNotes" to FieldValue.arrayUnion(note)
        )
        db.collection("Tickets").document(ticketId)
            .update(updates)
            .addOnSuccessListener {
                selectedTicketLiveData.value?.let { currentTicket ->
                    val updatedTicket = currentTicket.copy(
                        ticketStatus = newStatus,
                        ticketNotes = currentTicket.ticketNotes.apply { add(note) }
                    )
                    selectedTicketLiveData.postValue(updatedTicket)
                    addHistoryEntry(ticketId, "Cambio de estado a '${newStatus.getDisplayString()}', nota añadida: '$note'")
                    addNotificationForUser(currentTicket.ticketClientID, "El estado de tu ticket ha cambiado a '${newStatus.getDisplayString()}'.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating ticket status: ", e)
            }
    }

    fun unassignTicket(ticketId: String) {
        db.collection("Tickets").document(ticketId)
            .update("ticketEmployeeID", null)
            .addOnSuccessListener {
                addHistoryEntry(ticketId, "Empleado se dio de baja del ticket.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to unassign ticket: ", e)
            }
    }

    fun lockTicket(ticketId: String) {
        val updates = mapOf(
            "isLocked" to true
        )
        db.collection("Tickets").document(ticketId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Ticket locked successfully.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to lock ticket: ", e)
            }
    }

    fun unlockTicket(ticketId: String) {
        val updates = mapOf(
            "locked" to false
        )
        db.collection("Tickets").document(ticketId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Ticket unlocked successfully.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to unlock ticket: ", e)
            }
    }
}