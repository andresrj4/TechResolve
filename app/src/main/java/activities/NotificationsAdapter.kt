package activities

import util.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Notification

class NotificationAdapter(private var notifications: List<Notification>, private val userId: String, private val viewModel: TicketViewModel) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusIcon: ImageView = view.findViewById(R.id.notification_status_icon)
        val messageText: TextView = view.findViewById(R.id.notification_message)
        val timeText: TextView = view.findViewById(R.id.notification_time)

        fun bind(notification: Notification, userId: String) {
            messageText.text = notification.message
            timeText.text = DateUtils.getTimeAgo(notification.timestamp, System.currentTimeMillis())

            if (notification.isRead) {
                statusIcon.setImageResource(R.drawable.ic_notification_read)
            } else {
                statusIcon.setImageResource(R.drawable.ic_notifications_new)
                if (!notification.isRead) {
                    viewModel.markNotificationAsRead(userId, notification.id)
                    notification.isRead = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_list_items, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position], userId)  // Pass userId here
    }

    fun updateData(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notifications.size
}