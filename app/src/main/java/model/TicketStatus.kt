package model

import com.example.sistema_de_tickets.R

enum class TicketStatus {
    OPEN,
    CLOSED,
    PENDING,
    IN_PROGRESS,
    RESOLVED;

    fun getColor(): Int {
        return when (this) {
            OPEN -> R.color.dim_gray
            IN_PROGRESS -> R.color.copper
            PENDING -> R.color.tyrian_purple
            CLOSED -> R.color.falu_red
            RESOLVED -> R.color.british_racing_green
        }
    }
}