package com.gawel.sender.data.datasources

import android.content.Context

class SharedPrefsDataSource(context: Context) {
    val sharedPrefsName = "sending_info"
    val sharedPrefs = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)

    val sendingDateKey = "sending_date"

    fun getLastSendingDate() =
        sharedPrefs.getLong(sendingDateKey, 0)

    fun setLastSendingDate(date: Long) {
        sharedPrefs.edit().putLong(sendingDateKey, date).apply()
    }
}