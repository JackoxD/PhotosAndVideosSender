package com.gawel.sender.domain.usecases

import android.net.Uri
import com.gawel.core.models.Result
import com.gawel.sender.data.datasources.SharedPrefsDataSource
import com.gawel.sender.data.repositiories.PhotoSenderSocketRepositoryImpl
import com.gawel.sender.data.repositiories.PhotosRepositoryImpl
import com.gawel.sender.data.repositiories.SharedPrefsRepositoryImpl
import com.gawel.sender.domain.models.Photo
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.time.Instant

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi

class GetPhotosTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        // 1
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // 2
        Dispatchers.resetMain()
        // 3
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun getPhotosList_returnOneElementList() = runBlockingTest {
        val mock = mock(IPhotoRepository::class.java)
        val listOf = listOf(Photo())
        given(mock.getPhotosFromDevice(0)).willReturn(Result.SUCCESS(listOf))

        val invoke = GetPhotos(mock).invoke(0)
        if (invoke is Result.SUCCESS) {
            assertTrue(listOf[0] == invoke.data[0])
            assertTrue(listOf.size == invoke.data.size)
        }
        else
            fail("Data received is not equals")
    }

}