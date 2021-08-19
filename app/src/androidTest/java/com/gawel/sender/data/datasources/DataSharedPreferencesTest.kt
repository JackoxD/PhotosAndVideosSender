package com.gawel.sender.data.datasources

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @see SharedPrefsDataSource
 */
class DataSharedPreferencesTest {

    val applicationContext = ApplicationProvider.getApplicationContext<Context>()


    @Test
    fun save_date_to_shared_preferences__check_if_saved() = runBlocking {
        val savedDate = 20045L
        val dataSharedPreferences = SharedPrefsDataSource(applicationContext)
        dataSharedPreferences.setLastSendingDate(savedDate)
        val readDate = dataSharedPreferences.getLastSendingDate()
        Assert.assertEquals(savedDate, readDate)
    }
}