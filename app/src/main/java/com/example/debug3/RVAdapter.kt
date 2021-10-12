package com.example.debug3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(private var rv: ArrayList<ArrayList<String>>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return RVAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rvlist,parent,false )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val rvv = rv[position]

        holder.itemView.apply {
            findViewById<TextView>(R.id.tvWord).text = rvv[0]
            findViewById<TextView>(R.id.tvDefinition).text = rvv[1]
        }
    }

    override fun getItemCount() = rv.size

}