package activities

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import model.TicketStatus
import util.SpaceItemDecoration

class Home : Fragment() {
    private lateinit var viewModel: TicketViewModel
    private lateinit var pieChart: PieChart
    private lateinit var recentlyUpdatedAdapter: RecentlyUpdatedTicketAdapter
    private var currentUserRole: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(TicketViewModel::class.java)
        pieChart = view.findViewById(R.id.home_piechart)
        setupRecentlyUpdatedRecyclerView(view)
        initializePieChart()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userManager = UserManager.getInstance()
        viewModel.setupTicketListener()

        userManager.fetchUserRole(userId, { role ->
            currentUserRole = role
            if (currentUserRole == "Cliente") {
                viewModel.fetchTicketCountsByStatusForClient(userId)
                viewModel.fetchRecentlyUpdatedTickets(userId,"Cliente")
            } else {
                viewModel.fetchTicketCountsByStatusForEmployee(userId)
                viewModel.fetchRecentlyUpdatedTickets(userId,"Empleado")
            }
            observeViewModel()
        }, {
            pieChart.centerText = "Error al cargar datos"
        })

        viewModel.ticketStatusCountLiveData.observe(viewLifecycleOwner) { statusCountMap ->
            if (statusCountMap != null && statusCountMap.isNotEmpty()) {
                setupPieChart(statusCountMap)
            } else {
                pieChart.centerText = "No hay data disponible"
            }
        }
    }
    private fun observeViewModel() {
        viewModel.recentlyUpdatedTicketsLiveData.observe(viewLifecycleOwner) { updates ->
            recentlyUpdatedAdapter.updateData(updates)
        }
    }

    private fun setupRecentlyUpdatedRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recently_updated_recycler_view)
        recentlyUpdatedAdapter = RecentlyUpdatedTicketAdapter(mutableListOf()) { ticket ->
            // TODO: Navigate to ticket details
        }
        recyclerView.adapter = recentlyUpdatedAdapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val spaceInPixels = resources.getDimensionPixelSize(R.dimen.recently_updated_spacing)
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceInPixels))

        viewModel.recentlyUpdatedTicketsLiveData.observe(viewLifecycleOwner) { tickets ->
            recentlyUpdatedAdapter.updateData(tickets)
        }
    }

    private fun setupPieChart(data: Map<TicketStatus, Int>) {
        val entries = data.entries.map { PieEntry(it.value.toFloat(), it.key.getDisplayString()) }
        val dataSet = PieDataSet(entries, "").apply {
            colors = data.entries.map { ContextCompat.getColor(requireContext(), it.key.getColorForContainers()) }
            valueTextSize = 16f
            valueTextColor = Color.WHITE
            setDrawValues(true)
        }
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun initializePieChart() {
        pieChart.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 15f, 10f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = false
            setDrawCenterText(false)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.formSize = 10f
            legend.textSize = 12f
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.battleship_grey)
            animateY(1400, Easing.EaseInOutQuad)
        }
    }
}