package activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import com.google.firebase.auth.FirebaseAuth
import model.Notification

class Notifications : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var viewModel: TicketViewModel
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        userId = FirebaseAuth.getInstance().currentUser?.uid
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        recyclerView = view.findViewById(R.id.notifications_recycler_viewer)
        adapter = NotificationAdapter(listOf(), userId ?: "", viewModel)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.notificationLiveData.observe(viewLifecycleOwner) { notifications ->
            adapter.updateData(notifications)
            updateNotifications(notifications)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModel.fetchNotificationsForUser(userId)
        }
    }

    private fun updateNotifications(notifications: List<Notification>) {
        val sortedNotifications = notifications.sortedByDescending { it.timestamp }
        adapter.updateData(sortedNotifications)
        recyclerView.adapter = adapter
    }
}