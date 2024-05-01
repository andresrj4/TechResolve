package activities

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.HistoryEntry

class HistoryAdapter(private var historyEntries: MutableList<HistoryEntry>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = historyEntries[position]
        holder.view.text = entry.message
    }

    override fun getItemCount(): Int = historyEntries.size

    fun updateData(newEntries: List<HistoryEntry>) {
        historyEntries.clear()
        historyEntries.addAll(newEntries)
        notifyDataSetChanged()
    }
}