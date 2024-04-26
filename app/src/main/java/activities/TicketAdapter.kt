package activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Ticket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketsAdapter(private var tickets: MutableList<Ticket>, private val onItemClick: (Ticket) -> Unit) : RecyclerView.Adapter<TicketsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.client_list_ticket_title)
        val statusTextView: TextView = view.findViewById(R.id.client_list_ticket_status)
        val dateTextView: TextView = view.findViewById(R.id.client_list_ticket_date_created)

        fun bind(ticket: Ticket, clickListener: (Ticket) -> Unit) {
            titleTextView.text = ticket.ticketTitle
            statusTextView.text = ticket.ticketStatus.toString()
            dateTextView.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ticket.ticketDateCreated))
            itemView.setOnClickListener { clickListener(ticket) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tickets[position], onItemClick)
    }

    override fun getItemCount(): Int = tickets.size

    fun updateData(newTickets: List<Ticket>) {
        tickets.clear()
        tickets.addAll(newTickets)
        notifyDataSetChanged()
    }
}