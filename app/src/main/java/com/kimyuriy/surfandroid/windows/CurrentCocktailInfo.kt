package com.kimyuriy.surfandroid.windows

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.adapters.NonEditableIngredientAdapter
import com.kimyuriy.surfandroid.enums.OpenType
import com.kimyuriy.surfandroid.utils.CustomFunctions
import com.kimyuriy.surfandroid.utils.SPValues
import org.json.JSONArray

class CurrentCocktailInfo : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_cocktail_info)

        val nameTV: TextView = findViewById(R.id.CCI_Name_TV)
        val descriptionTV: TextView = findViewById(R.id.CCI_Description_TV)
        val recipeTV: TextView = findViewById(R.id.CCI_CocktailRecipe_TV)
        val ingredientsRV: RecyclerView = findViewById(R.id.CCI_Ingredients_RV)

        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("desc")
        val ingredients = ArrayList(intent.getStringExtra("ingredients")?.split(","))
        val recipe = intent.getStringExtra("recipe")

        nameTV.text = name
        if (description != "") descriptionTV.text = description else descriptionTV.visibility = View.GONE
        if (recipe != "") recipeTV.text = recipe else recipeTV.visibility = View.GONE

        prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)

        ingredientsRV.apply {
            layoutManager = LinearLayoutManager(this@CurrentCocktailInfo)
            adapter = NonEditableIngredientAdapter(ingredients)
        }

        findViewById<Button>(R.id.CCI_EditInfo_B).setOnClickListener {
            val intent = Intent(this@CurrentCocktailInfo, CreateCocktail::class.java)
            intent.apply {
                putExtra("name", name)
                putExtra("desc", description)
                putExtra("ingredients", ingredients.joinToString(","))
                putExtra("recipe", recipe)
                putExtra("OpenType", OpenType.EDIT.name)
            }
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.CCI_DeleteCocktail_B).setOnClickListener {
            AlertDialog.Builder(this@CurrentCocktailInfo).apply {
                setTitle(getString(R.string.attention_text))
                setMessage(getString(R.string.do_you_really_want_to_delete_this_cocktail_text))
                setPositiveButton(getString(R.string.yes_text)) { _, _ ->
                    deleteCocktail(name.toString())
                    openNecessaryWindow()
                }
                setNegativeButton(getString(R.string.no_text), null)
            }.create().show()
        }
    }

    @Override
    override fun onBackPressed() {
        openNecessaryWindow()
    }

    private fun openNecessaryWindow() {
        val intent = if (prefs.getString(SPValues.savedCocktailsKey, null).isNullOrEmpty()) {
            Intent(this@CurrentCocktailInfo, AddFirstCocktail::class.java)
        }
        else {
            Intent(this@CurrentCocktailInfo, SavedCocktails::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun deleteCocktail(name: String) {
        val saved = prefs.getString(SPValues.savedCocktailsKey, null)
        if (saved != null) {
            val json = JSONArray(saved)
            val index = CustomFunctions.getItemIndex(name, json, this@CurrentCocktailInfo)
            if (index != -1) {
                json.remove(index)
            }
            val stringToPut = if (json.length() <= 0) null else json.toString()
            prefs.edit().putString(SPValues.savedCocktailsKey, stringToPut).apply()
        }
    }
}