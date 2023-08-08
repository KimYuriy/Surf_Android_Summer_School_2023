package com.kimyuriy.surfandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.dataclasses.CocktailInfo

class SavedCocktailsRecAdapter(private val array: ArrayList<CocktailInfo>): RecyclerView.Adapter<SavedCocktailsRecAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.SCI_CocktailName_TV)

        init {
            itemView.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.saved_cocktail_item, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            nameTV.text = array[position].name
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }
}