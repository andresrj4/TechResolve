package activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.RecentUpdatesTickets
import util.DateUtils

class RecentlyUpdatedTicketAdapter(
    private var updates: MutableList<RecentUpdatesTickets>,
    private val onItemClick: (RecentUpdatesTickets) -> Unit
) : RecyclerView.Adapter<RecentlyUpdatedTicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.recently_updated_ticket_title)
        private val messageTextView: TextView = view.findViewById(R.id.recently_updated_message)
        private val timeTextView: TextView = view.findViewById(R.id.recently_updated_time)

        fun bind(update: RecentUpdatesTickets, onItemClick: (RecentUpdatesTickets) -> Unit) {
            titleTextView.text = update.ticketTitle
            messageTextView.text = update.updateMessage
            timeTextView.text = DateUtils.getTimeAgo(update.timestamp, System.currentTimeMillis())

            itemView.setOnClickListener { onItemClick(update) }
            itemView.background = ContextCompat.getDrawable(itemView.context, update.ticketStatus.setRecentlyUpdatedBackground())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recently_updated_ticket_item, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(updates[position], onItemClick)
    }

    override fun getItemCount(): Int = updates.size

    fun updateData(newUpdates: List<RecentUpdatesTickets>) {
        updates.clear()
        updates.addAll(newUpdates)
        notifyDataSetChanged()
    }
}