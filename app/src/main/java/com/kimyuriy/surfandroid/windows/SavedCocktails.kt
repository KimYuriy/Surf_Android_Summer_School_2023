package com.kimyuriy.surfandroid.windows

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.adapters.SavedCocktailsRecAdapter
import com.kimyuriy.surfandroid.dataclasses.CocktailInfo
import com.kimyuriy.surfandroid.enums.OpenType
import com.kimyuriy.surfandroid.utils.SPValues
import org.json.JSONArray

class SavedCocktails : AppCompatActivity() {
    private lateinit var savedCocktailsRV: RecyclerView
    private lateinit var customAdapter: SavedCocktailsRecAdapter
    private lateinit var prefs: SharedPreferences
    private val cocktails: ArrayList<CocktailInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_cocktails)

        savedCocktailsRV = findViewById(R.id.SC_SavedCocktails_RV)
        prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)

        /**
         * Установка адаптера для RecyclerView
         */
        customAdapter = SavedCocktailsRecAdapter(cocktails)
        savedCocktailsRV.apply {
            layoutManager = GridLayoutManager(this@SavedCocktails, 2)
            adapter = customAdapter
        }

        /**
         * Нажатие кнопки открытия окна создания нового коктейля. Параметром передается тип
         * действия (создание нового коктейля)
         */
        findViewById<ImageButton>(R.id.SC_AddNewCocktail_IB).setOnClickListener {
            val intent = Intent(this@SavedCocktails, CreateCocktail::class.java).apply {
                putExtra("OpenType", OpenType.CREATE.name)
            }
            startActivity(intent)
        }
    }

    /**
     * Загрузка сохраненных данных из SharedPreferences, их парсинг и их установка в массив, также
     * оповещение адаптера об изменениях в массиве
     */
    override fun onResume() {
        super.onResume()
        val savedCocktails = prefs.getString(SPValues.savedCocktailsKey, null)
        if (savedCocktails != null) {
            cocktails.clear()
            val jsonArray = JSONArray(savedCocktails)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val desc = jsonObject.getString("description")
                val ingredients = jsonObject.getString("ingredients")
                val recipe = jsonObject.getString("recipe")
                cocktails.add(CocktailInfo(name, desc, ingredients, recipe))
            }
            customAdapter.notifyDataSetChanged()
        }
    }
}