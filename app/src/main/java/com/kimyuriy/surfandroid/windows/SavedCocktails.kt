package com.kimyuriy.surfandroid.windows

import android.content.Context
import android.content.Intent
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_cocktails)

        savedCocktailsRV = findViewById(R.id.SC_SavedCocktails_RV)

        findViewById<ImageButton>(R.id.SC_AddNewCocktail_IB).setOnClickListener {
            val intent = Intent(this@SavedCocktails, CreateCocktail::class.java).apply {
                putExtra("OpenType", OpenType.CREATE.name)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)
        val savedCocktails = prefs.getString(SPValues.savedCocktailsKey, null)
        if (savedCocktails != null) {
            val jsonArray = JSONArray(savedCocktails)
            val cocktails: ArrayList<CocktailInfo> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val desc = jsonObject.getString("description")
                val ingredients = jsonObject.getString("ingredients")
                val recipe = jsonObject.getString("recipe")
                cocktails.add(CocktailInfo(name, desc, ingredients, recipe))
            }
            customAdapter = SavedCocktailsRecAdapter(cocktails)
            savedCocktailsRV.apply {
                layoutManager = GridLayoutManager(this@SavedCocktails, 2)
                adapter = customAdapter
            }
        }
    }
}