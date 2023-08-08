package com.kimyuriy.surfandroid.windows

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.adapters.NonEditableIngredientAdapter

class CurrentCocktailInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_cocktail_info)

        val nameTV: TextView = findViewById(R.id.CCI_Name_TV)
        val descriptionTV: TextView = findViewById(R.id.CCI_Description_TV)
        val recipeTV: TextView = findViewById(R.id.CCI_CocktailRecipe_TV)
        val ingredientsRV: RecyclerView = findViewById(R.id.CCI_Ingredients_RV)
        val editB: Button = findViewById(R.id.CCI_EditInfo_B)
        val deleteB: Button = findViewById(R.id.CCI_DeleteCocktail_B)

        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("desc")
        val ingredients = ArrayList(intent.getStringExtra("ingredients")?.split(","))
        val recipe = intent.getStringExtra("recipe")

        nameTV.text = name

        if (description != "") descriptionTV.text = description else descriptionTV.visibility = View.GONE
        if (recipe != "") recipeTV.text = recipe else recipeTV.visibility = View.GONE

        val mAdapter = NonEditableIngredientAdapter(ingredients)
        ingredientsRV.apply {
            layoutManager = LinearLayoutManager(this@CurrentCocktailInfo)
            adapter = mAdapter
        }
    }
}