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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.adapters.IngredientsRecAdapter
import com.kimyuriy.surfandroid.enums.OpenType
import com.kimyuriy.surfandroid.utils.CustomFunctions
import com.kimyuriy.surfandroid.utils.SPValues
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class CreateCocktail : AppCompatActivity(), IngredientsRecAdapter.OnIngredientDeleteListener {
    private var ingredients = ArrayList<String>()
    private lateinit var recAdapter: IngredientsRecAdapter
    private lateinit var oldName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_cocktail)

        val cocktailNameET: EditText = findViewById(R.id.CC_CocktailName_ET)
        val cocktailRecipeET: EditText = findViewById(R.id.CC_CocktailRecipe_ET)
        val descriptionET: EditText = findViewById(R.id.CC_Description_TV)
        val saveB: Button = findViewById(R.id.CC_Save_B)
        val ingredientsRV: RecyclerView = findViewById(R.id.CC_Ingredients_RV)

        /**
         * Проверка на тип действия - если редактирование существующих данных, то подгружаются все
         * необходимые данные из intent, устанавливаются в соответствующие поля и переменные,
         * все необходимые элементы экрана становятся активными и видимыми. Также сохраняется
         * предыдущее имя изменяемого элемента
         */
        val openType = OpenType.valueOf(intent.getStringExtra("OpenType").toString())
        if (openType == OpenType.EDIT) {
            cocktailNameET.setText(intent.getStringExtra("name"))
            oldName = intent.getStringExtra("name").toString()
            descriptionET.setText(intent.getStringExtra("desc"))
            cocktailRecipeET.setText(intent.getStringExtra("recipe"))
            if (!intent.getStringExtra("ingredients").isNullOrEmpty()) {
                ingredients = ArrayList(intent.getStringExtra("ingredients").toString().split(","))
                ingredientsRV.visibility = View.VISIBLE
            }
            cocktailRecipeET.isEnabled = true
            saveB.isEnabled = true
        }

        /**
         * Установка адаптера для RecyclerView, также назначается слушатель для интерфейса
         */
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
         * Если происходит создание нового коктейля, то он просто сохраняется в JSON-объект и,
         * если уже имеются сохраненные данные, то добавляется в конец, иначе создается новый массив
         * и в него помещается объект. Если же происходит редактирование, то создается объект с
         * новыми данными, удаляется старый и на его место вставляется новый. Весь список сохраняется
         * в SharedPreferences
         */
        saveB.setOnClickListener {
            val prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)
            val savedCocktailsString = prefs.getString(SPValues.savedCocktailsKey, null)
            var jsonString: String? = ""

            val name = cocktailNameET.text.toString()
            val recipe = cocktailRecipeET.text.toString().ifEmpty { "" }
            val desc = descriptionET.text.toString().ifEmpty { "" }

            val cocktailJSON = JSONObject().apply {
                put("name", name)
                put("description", desc)
                put("ingredients", ingredients.joinToString(","))
                put("recipe", recipe)
            }

            if (openType == OpenType.CREATE) {
                jsonString = if (savedCocktailsString.isNullOrEmpty()) {
                    val jsonArray = JSONArray()
                    jsonArray.put(cocktailJSON)
                    jsonArray.toString()
                } else {
                    val savedCocktails = JSONArray(savedCocktailsString)
                    if (CustomFunctions.getItemIndex(name, savedCocktails, this@CreateCocktail) == -1) {
                        savedCocktails.put(cocktailJSON)
                    }
                    else {
                        Toast.makeText(this@CreateCocktail,
                            getString(R.string.already_exist_text), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    savedCocktails.toString()
                }
            }
            else {
                val json = JSONArray(savedCocktailsString)
                val index = CustomFunctions.getItemIndex(oldName, json, this@CreateCocktail)
                if (index != -1) {
                    json.apply {
                        remove(index)
                        put(index, cocktailJSON)
                    }
                }
                jsonString = if (json.length() <= 0) null else json.toString()
            }
            prefs.edit().putString(SPValues.savedCocktailsKey, jsonString).apply()
            openNecessaryWindow()
        }

        /**
         * Нажатие кнопки возврата назад
         */
        findViewById<Button>(R.id.CC_Cancel_B).setOnClickListener {
            openNecessaryWindow()
        }

        /**
         * Нажатие кнопки добавления нового ингредиента.
         * Название ингредиента берется из EditText и добавляется в массив ингредиентов, RecyclerView,
         * в котором отображаются ингредиенты, становится видимым
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

    /**
     * Переопределенный метод удаления ингредиента из массива ингредиентов
     */
    override fun onIngredientDelete(ingredient: String) {
        ingredients.remove(ingredient)
        recAdapter.notifyDataSetChanged()
    }

    /**
     * Функция открытия окна. Проверяет сохраненный массив - если он пустой, то открывается стартовое окно,
     * а иначе окно с сохраненными коктейлями
     */
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