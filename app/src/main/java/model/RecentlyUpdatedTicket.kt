package model

data class RecentUpdatesTickets(
    val ticketId: String,
    val ticketTitle: String,
    val updateMessage: String,
    val timestamp: Long,
    val ticketStatus: TicketStatus  // Ensure this field is available
)