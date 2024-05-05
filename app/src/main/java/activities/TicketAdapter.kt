package activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Ticket

class TicketAdapter(
    private var tickets: MutableList<Ticket>,
    private val isEmployee: Boolean,
    private val onItemClick: (Ticket) -> Unit,
    private val onDetailsClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.ticket_list_ticket_title)
        val statusTextView: TextView = view.findViewById(R.id.ticket_list_ticket_status)
        val detailsLink: TextView = view.findViewById(R.id.ticket_list_ticket_details_link)

        fun bind(ticket: Ticket, clickListener: (Ticket) -> Unit, detailsClickListener: (Ticket) -> Unit) {
            titleTextView.text = ticket.ticketTitle
            statusTextView.text = ticket.ticketStatus.getDisplayString()
            val colorId = ticket.ticketStatus.getColorForText()
            statusTextView.setTextColor(ContextCompat.getColor(view.context, colorId))

            itemView.setOnClickListener { clickListener(ticket) }
            detailsLink.setOnClickListener { detailsClickListener(ticket) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tickets[position], onItemClick, onDetailsClick)
    }

    override fun getItemCount(): Int = tickets.size

    fun updateData(newTickets: List<Ticket>) {
        tickets.clear()
        tickets.addAll(newTickets)
        notifyDataSetChanged()
    }
}