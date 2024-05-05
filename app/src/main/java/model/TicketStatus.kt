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

    fun getColorForText(): Int {
        return when (this) {
            OPEN -> R.color.dim_gray
            IN_PROGRESS -> R.color.ochre
            PENDING -> R.color.dogwood_rose
            CLOSED -> R.color.fire_engine_red
            RESOLVED -> R.color.shamrock_green
        }
    }

    fun getColorForContainers(): Int {
        return when (this) {
            OPEN -> R.color.dim_gray
            IN_PROGRESS -> R.color.ochre
            PENDING -> R.color.tyrian_purple
            CLOSED -> R.color.falu_red
            RESOLVED -> R.color.castleton_green
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

    fun setRecentlyUpdatedBackground(): Int {
        return when (this) {
            OPEN -> R.drawable.ticket_open_container
            IN_PROGRESS -> R.drawable.ticket_in_progress_container
            PENDING -> R.drawable.ticket_pending_container
            CLOSED -> R.drawable.ticket_closed_container
            RESOLVED -> R.drawable.ticket_resolved_container
        }
    }
}