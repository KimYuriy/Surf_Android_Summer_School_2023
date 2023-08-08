package com.kimyuriy.surfandroid.windows

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.adapters.IngredientsRecAdapter
import com.kimyuriy.surfandroid.enums.OpenType
import com.kimyuriy.surfandroid.utils.SPValues
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class CreateCocktail : AppCompatActivity(), IngredientsRecAdapter.OnIngredientDeleteListener {
    private val ingredients = ArrayList<String>()
    private lateinit var recAdapter: IngredientsRecAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_cocktail)

        val cocktailNameET: EditText = findViewById(R.id.CC_CocktailName_ET)
        val cocktailRecipeET: EditText = findViewById(R.id.CC_CocktailRecipe_ET)
        val descriptionET: EditText = findViewById(R.id.CC_Description_TV)
        val saveB: Button = findViewById(R.id.CC_Save_B)
        val ingredientsRV: RecyclerView = findViewById(R.id.CC_Ingredients_RV)

        val openType = OpenType.valueOf(intent.getStringExtra("OpenType").toString())

        recAdapter = IngredientsRecAdapter(ingredients)
        recAdapter.setOnIngredientDeleteListener(this@CreateCocktail)
        ingredientsRV.apply{
            layoutManager = LinearLayoutManager(this@CreateCocktail)
            adapter = recAdapter
        }

        /**
         * Проверка поля названия коктейля на пустоту. Если пустое - кнопка сохранения неактивна,
         * в противном случае активна
         */
        cocktailNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                saveB.isEnabled = cocktailNameET.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        /**
         * Нажатие кнопки сохранения информации о коктейле
         */
        saveB.setOnClickListener {
            val name = cocktailNameET.text.toString()
            val recipe = cocktailRecipeET.text.toString().ifEmpty { "" }
            val desc = descriptionET.text.toString().ifEmpty { "" }
            val cocktailJSON = JSONObject().apply {
                put("name", name)
                put("description", desc)
                put("ingredients", ingredients.joinToString(","))
                put("recipe", recipe)
            }
            val prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)
            val savedCocktailsString = prefs.getString(SPValues.savedCocktailsKey, null)
            val jsonString = if (savedCocktailsString.isNullOrEmpty()) {
                val jsonArray = JSONArray().put(cocktailJSON)
                jsonArray.toString()
            } else {
                val savedCocktails = JSONArray(savedCocktailsString)
                savedCocktails.put(cocktailJSON)
                savedCocktails.toString()
            }
            prefs.edit().putString(SPValues.savedCocktailsKey, jsonString).apply()
            cocktailNameET.text = null
            cocktailRecipeET.text = null
            descriptionET.text = null
            cocktailRecipeET.isEnabled = false
            ingredients.clear()
            recAdapter.notifyDataSetChanged()
            ingredientsRV.visibility = View.GONE
        }

        /**
         * Нажатие кнопки возврата назад
         */
        findViewById<Button>(R.id.CC_Cancel_B).setOnClickListener {
            openNecessaryWindow()
        }

        /**
         * Нажатие кнопки добавления нового ингредиента
         */
        findViewById<Button>(R.id.CC_AddIngredient_B).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.add_ingredient_alert, null)
            AlertDialog.Builder(this@CreateCocktail).apply {
                setView(dialogView)
                setPositiveButton(getString(R.string.add_ingredient_text)) { dialog, _ ->
                    val ingredient = dialogView.findViewById<EditText>(R.id.ADA_Ingredient_ET).text.toString()
                    if (ingredient.isNotEmpty()) {
                        ingredients.add(ingredient)
                        cocktailRecipeET.isEnabled = true
                        recAdapter.notifyItemInserted(ingredients.size - 1)
                        if (ingredientsRV.visibility == View.GONE) {
                            ingredientsRV.visibility = View.VISIBLE
                        }
                        dialog.cancel()
                    }
                }
                setNegativeButton(getString(R.string.cancel_text), null)
            }.create().show()
        }
    }

    @Override
    override fun onBackPressed() {
        openNecessaryWindow()
    }

    override fun onIngredientDelete(ingredient: String) {
        ingredients.remove(ingredient)
        recAdapter.notifyDataSetChanged()
    }

    private fun openNecessaryWindow() {
        val prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)
        val intent = if (prefs.getString(SPValues.savedCocktailsKey, null) != null)
            Intent(this@CreateCocktail, SavedCocktails::class.java)
        else {
            Intent(this@CreateCocktail, AddFirstCocktail::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}