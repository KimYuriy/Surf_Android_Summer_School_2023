package com.kimyuriy.surfandroid.windows

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.kimyuriy.surfandroid.R
import com.kimyuriy.surfandroid.enums.OpenType
import com.kimyuriy.surfandroid.utils.SPValues

class AddFirstCocktail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_first_cocktail)

        val prefs = getSharedPreferences(SPValues.prefsName, Context.MODE_PRIVATE)
        val savedCocktails = prefs.getString(SPValues.savedCocktailsKey, null)
        if (savedCocktails != null) {
            startActivity(Intent(this@AddFirstCocktail, SavedCocktails::class.java))
            finish()
        }

        /**
         * Нажатие кнопки "+" для добавления первого коктейля
         */
        findViewById<ImageButton>(R.id.AFC_AddFirstCocktail_IB).setOnClickListener {
            val intent = Intent(this@AddFirstCocktail, CreateCocktail::class.java).apply {
                putExtra("OpenType", OpenType.CREATE.name)
            }
            startActivity(intent)
            finish()
        }
    }

    /**
     * Показ диалогового окна для подтверждения выхода из приложения
     */
    @Override
    override fun onBackPressed() {
        AlertDialog.Builder(this@AddFirstCocktail).apply {
            setTitle(getString(R.string.attention_text))
            setMessage(getString(R.string.do_you_really_wanna_exit_app_text))
            setPositiveButton(getString(R.string.yes_text)) { _, _ -> finish() }
            setNegativeButton(getString(R.string.no_text), null)
            setCancelable(true)
        }.create().show()
    }
}