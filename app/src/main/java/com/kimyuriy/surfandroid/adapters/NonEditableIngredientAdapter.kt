package com.kimyuriy.surfandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R

class NonEditableIngredientAdapter(private val array: ArrayList<String>): RecyclerView.Adapter<NonEditableIngredientAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.NEII_RecipeName_TV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.non_editable_ingredient_item, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            nameTV.text = array[position]
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }
}