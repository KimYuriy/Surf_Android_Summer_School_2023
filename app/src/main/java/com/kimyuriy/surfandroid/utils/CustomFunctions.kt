package com.kimyuriy.surfandroid.utils

import android.content.Context
import org.json.JSONArray

object CustomFunctions {
    /**
     * Функция нахождения индекса элемента по полю "name" в переданном параметром массиве. При нахождении
     * возвращает индекс найденного элемента, иначе -1
     */
    fun getItemIndex(name: String, array: JSONArray, context: Context): Int {
        for (i in 0 until array.length()) {
            if (array.getJSONObject(i).getString("name") == name) {
                return i
            }
        }
        return -1
    }
}