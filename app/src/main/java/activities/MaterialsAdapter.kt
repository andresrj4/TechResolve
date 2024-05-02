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
        val nameTextView: TextView = view.findViewById(R.id.material_name)
        val quantityTextView: TextView = view.findViewById(R.id.material_quantity)
        val priceTextView: TextView = view.findViewById(R.id.material_price)
        val totalTextView: TextView = view.findViewById(R.id.material_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.material_list_items, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        if (material.isTotal) {
            holder.nameTextView.text = "Total"
            holder.quantityTextView.text = ""
            holder.priceTextView.text = ""
            holder.totalTextView.text = String.format("%.2f", material.total)
        } else {
            holder.nameTextView.text = material.name
            holder.quantityTextView.text = material.quantity.toString()
            holder.priceTextView.text = String.format("%.2f", material.price)
            holder.totalTextView.text = String.format("%.2f", material.total)
        }
    }

    override fun getItemCount(): Int = materials.size

    fun addMaterial(material: Material) {
        materials.add(material)
        notifyItemInserted(materials.size - 1)
    }

    fun updateMaterials(materialsList: List<Material>) {
        materials.clear()
        materials.addAll(materialsList)
        notifyDataSetChanged()
    }
}