package model

import java.util.Date
import java.util.UUID

data class Ticket(
    val ticketID: String = UUID.randomUUID().toString(),  // Generates a unique ID
    val ticketTitle: String = "",
    val ticketDescription: String = "",
    val ticketDateCreated: Long = System.currentTimeMillis(),
    var ticketDateClosed: Long? = null,
    val ticketClientID: String = "",
    var ticketEmployeeID: String? = null,
    var ticketStatus: TicketStatus = TicketStatus.OPEN,
    val ticketNotes: MutableList<String> = mutableListOf(),
    val ticketMaterialsUsed: MutableList<Material> = mutableListOf(),
    val ticketHistory: MutableList<HistoryEntry> = mutableListOf()
)

data class Material(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class HistoryEntry(
    val timestamp: Long = 0L,
    val message: String = "",
    val userId: String = ""
)