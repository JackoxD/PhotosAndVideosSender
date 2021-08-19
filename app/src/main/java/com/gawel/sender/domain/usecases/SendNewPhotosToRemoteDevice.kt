package com.gawel.sender.domain.usecases

import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.core.models.UnknownError
import com.gawel.core.usecases.BaseUseCase
import com.gawel.core.usecases.BaseUseCaseNoParams
import com.gawel.sender.domain.models.Photo
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.gawel.sender.domain.repositories.IPhotoSenderSocketRepository
import com.gawel.sender.domain.repositories.ISharedPrefsRepository
import dagger.hilt.EntryPoint

private class GetLastSendingPhotoDate(private val sharedPreferences: ISharedPrefsRepository) :
    BaseUseCaseNoParams<Long>() {
    override suspend operator fun invoke() =
        sharedPreferences.getLastSendingDate()
}

class GetPhotos(private val photoRepository: IPhotoRepository) :
    BaseUseCase<List<Photo>, Long>() {
    override suspend operator fun invoke(params: Long) =
        photoRepository.getPhotosFromDevice(params)
}

private class SetLastSendingTime(private val sharedPreference: ISharedPrefsRepository) {
    suspend operator fun invoke(date: Long) {
        sharedPreference.setLastSendingDate(date)
    }
}

private class SendPhotosBySocket(private val photoSenderSocketRepository: IPhotoSenderSocketRepository) {
    suspend operator fun invoke(photos: List<Photo>) =
        photoSenderSocketRepository.sendPhotos(photos)
}

class SendNewPhotosToRemoteDevice(
    private val sharedPreference: ISharedPrefsRepository,
    private val photoRepository: IPhotoRepository,
    private val photoSenderSocketRepository: IPhotoSenderSocketRepository
) : BaseUseCaseNoParams<Boolean>() {
    override suspend fun invoke(): Result<Failure, Boolean> {
        val lastSendedPhotoDateResult = GetLastSendingPhotoDate(sharedPreference).invoke()
        if (lastSendedPhotoDateResult is Result.SUCCESS) {
            val lastSendedPhotoDate = lastSendedPhotoDateResult.data
            val getPhotosResult = GetPhotos(photoRepository).invoke(lastSendedPhotoDate)
            if (getPhotosResult is Result.SUCCESS) {
                val photosList = getPhotosResult.data
                val sendPhotosResult =
                    SendPhotosBySocket(photoSenderSocketRepository).invoke(photosList)
                if (sendPhotosResult is Result.SUCCESS)
                    SetLastSendingTime(sharedPreference).invoke(sendPhotosResult.data)
                else if (sendPhotosResult is Result.ERROR)
                    return Result.ERROR(sendPhotosResult.throwable)
                return Result.SUCCESS(true)
            } else if (getPhotosResult is Result.ERROR)
                return Result.ERROR(getPhotosResult.throwable)
        } else if (lastSendedPhotoDateResult is Result.ERROR) {
            return Result.ERROR(lastSendedPhotoDateResult.throwable)
        }
        return Result.ERROR(UnknownError())
    }
}

