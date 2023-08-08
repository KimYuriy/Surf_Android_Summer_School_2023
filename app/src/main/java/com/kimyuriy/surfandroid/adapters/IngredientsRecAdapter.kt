package com.kimyuriy.surfandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R

class IngredientsRecAdapter(private val ingredients: List<String>): RecyclerView.Adapter<IngredientsRecAdapter.ViewHolder>() {

    interface OnIngredientDeleteListener {
        fun onIngredientDelete(ingredient: String)
    }

    private var onDeleteListener: OnIngredientDeleteListener? = null
    fun setOnIngredientDeleteListener(listener: OnIngredientDeleteListener) {
        onDeleteListener = listener
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val deleteIngredientIB: ImageButton = itemView.findViewById(R.id.II_DeleteIngredient_IB)
        val ingredientNameTV: TextView = itemView.findViewById(R.id.II_IngredientName_TV)

        init {
            deleteIngredientIB.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onDeleteListener?.onIngredientDelete(ingredients[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, null)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            ingredientNameTV.text = ingredients[position]
        }
    }
}