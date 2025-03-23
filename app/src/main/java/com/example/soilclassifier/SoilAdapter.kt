package com.example.soilclassifier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter

class SoilAdapter(private val context: Context, private val soilList: List<Soil>) : BaseAdapter() {

    override fun getCount(): Int = soilList.size
    override fun getItem(position: Int): Any = soilList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_soil, parent, false)

        val soil = soilList[position]

        val nameTextView = view.findViewById<TextView>(R.id.soilName)
        val plantingTextView = view.findViewById<TextView>(R.id.plantingSeason)
        val fertilizersTextView = view.findViewById<TextView>(R.id.fertilizers)
        val irrigationTextView = view.findViewById<TextView>(R.id.irrigationTips)

        nameTextView.text = soil.name
        plantingTextView.text = "🌱 Planting: ${soil.plantingSeason}"
        fertilizersTextView.text = "💡 Fertilizers: ${soil.fertilizers}"
        irrigationTextView.text = "🚰 Irrigation: ${soil.irrigationTips}"

        return view
    }
}
