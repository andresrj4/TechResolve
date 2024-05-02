package model

import com.example.sistema_de_tickets.R
import java.io.Serializable

enum class TicketStatus : Serializable {
    OPEN, IN_PROGRESS, PENDING, CLOSED, RESOLVED;

    fun getDisplayString(): String {
        return when (this) {
            OPEN -> "Abierto"
            IN_PROGRESS -> "En proceso"
            PENDING -> "Estancado"
            CLOSED -> "Cerrado"
            RESOLVED -> "Resuelto"
        }
    }

    fun getColor(): Int {
        return when (this) {
            OPEN -> R.color.dim_gray
            IN_PROGRESS -> R.color.copper
            PENDING -> R.color.tyrian_purple
            CLOSED -> R.color.falu_red
            RESOLVED -> R.color.british_racing_green
        }
    }

    fun getBackgroundResource(): Int {
        return when (this) {
            OPEN -> R.drawable.btn_ticket_status_open
            IN_PROGRESS -> R.drawable.btn_ticket_status_in_progress
            PENDING -> R.drawable.btn_ticket_status_pending
            CLOSED -> R.drawable.btn_ticket_status_closed
            RESOLVED -> R.drawable.btn_ticket_status_resolved
        }
    }
}