package activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema_de_tickets.R
import model.Material

class MaterialsAdapter(private val materials: MutableList<Material>) :
    RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder>() {

    class MaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.material_name)
        private val quantityTextView: TextView = view.findViewById(R.id.material_quantity)
        private val priceTextView: TextView = view.findViewById(R.id.material_price)
        private val totalTextView: TextView = view.findViewById(R.id.material_total)

        fun bind(material: Material) {
            nameTextView.text = material.name
            quantityTextView.text = material.quantity.toString()
            priceTextView.text = String.format("%.2f", material.price)
            totalTextView.text = String.format("%.2f", material.total)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.material_list_items, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(materials[position])
    }

    override fun getItemCount(): Int = materials.size

    fun addMaterial(material: Material) {
        materials.add(material)
        notifyItemInserted(materials.size - 1)
    }
}