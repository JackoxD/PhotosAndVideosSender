package com.gawel.sender.data.datasources

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class PhotosInPhoneMemoryDataSourceTest {

    val applicationContext = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun getPhotosFromDevice_checkIfNoErrors() = runBlocking {
        val photosInPhoneMemoryDataSource = PhotosInPhoneMemoryDataSource(applicationContext)
        val photosFromDevice = photosInPhoneMemoryDataSource.getPhotosFromDevice(0)
        Assert.assertNotNull(photosFromDevice)
    }
}