package com.example.sistema_de_tickets.models

data class Ticket(
    val title: String,
    val description: String,
    val dateCreated: Long,
    var dateClosed: Long? = null,
    val clientID: String,
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
