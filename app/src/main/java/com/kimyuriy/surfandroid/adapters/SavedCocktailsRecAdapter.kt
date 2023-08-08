package com.kimyuriy.surfandroid.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.dataclasses.CocktailInfo
import com.kimyuriy.surfandroid.windows.CurrentCocktailInfo

class SavedCocktailsRecAdapter(private val array: ArrayList<CocktailInfo>): RecyclerView.Adapter<SavedCocktailsRecAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.SCI_CocktailName_TV)
        var ingredients = ""
        var desc = ""
        var recipe = ""

        init {
            /**
             * Нажатие на элемент для открытия странички с подробным описанием коктейля.
             * Передаются все необходимые данные в intent
             */
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CurrentCocktailInfo::class.java)
                intent.apply {
                    putExtra("name", nameTV.text.toString())
                    putExtra("desc", desc)
                    putExtra("ingredients", ingredients)
                    putExtra("recipe", recipe)
                }
                itemView.context.startActivity(intent)
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
            ingredients = array[position].ingredients
            desc = array[position].description
            recipe = array[position].recipe
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }
}