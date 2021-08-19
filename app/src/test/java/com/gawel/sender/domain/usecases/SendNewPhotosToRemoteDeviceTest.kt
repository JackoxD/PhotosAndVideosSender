package com.gawel.sender.domain.usecases

import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.core.models.UnknownError
import com.gawel.sender.domain.models.Photo
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.gawel.sender.domain.repositories.IPhotoSenderSocketRepository
import com.gawel.sender.domain.repositories.ISharedPrefsRepository
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class SendNewPhotosToRemoteDeviceTest {

    @Test
    fun getLastDateGetPhotosSendAndSaveDate_whenSuccess() = runBlockingTest {
        val date = 0L
        val sharedPrefs = mock(ISharedPrefsRepository::class.java)
        given(sharedPrefs.getLastSendingDate()).willReturn(Result.SUCCESS(date))
        val photoRepo = mock(IPhotoRepository::class.java)
        given(photoRepo.getPhotosFromDevice(date)).willReturn(Result.SUCCESS(listOf(Photo())))
        val photoSenderSocketRepo = mock(IPhotoSenderSocketRepository::class.java)
        given(photoSenderSocketRepo.sendPhotos(listOf(Photo()))).willReturn(Result.SUCCESS(10))

        val sendNewPhotosToRemoteDevice = SendNewPhotosToRemoteDevice(
            sharedPrefs,
            photoRepo,
            photoSenderSocketRepo
        )

        val invoke = sendNewPhotosToRemoteDevice.invoke()

        assertTrue(invoke is Result.SUCCESS)
    }

    @Test
    fun getLastDateGetPhotosSendAndSaveDate_whenError() = runBlockingTest {
        val date = -10L
        val sharedPrefs = mock(ISharedPrefsRepository::class.java)
        given(sharedPrefs.getLastSendingDate()).willReturn(Result.ERROR(UnknownError()))
        val photoRepo = mock(IPhotoRepository::class.java)
        given(photoRepo.getPhotosFromDevice(date)).willReturn(Result.SUCCESS(listOf(Photo())))
        val photoSenderSocketRepo = mock(IPhotoSenderSocketRepository::class.java)
        given(photoSenderSocketRepo.sendPhotos(listOf(Photo()))).willReturn(Result.SUCCESS(10))

        val sendNewPhotosToRemoteDevice = SendNewPhotosToRemoteDevice(
            sharedPrefs,
            photoRepo,
            photoSenderSocketRepo
        )

        val invoke = sendNewPhotosToRemoteDevice.invoke()

        assertTrue(invoke is Result.ERROR && invoke.throwable is UnknownError)
    }
}