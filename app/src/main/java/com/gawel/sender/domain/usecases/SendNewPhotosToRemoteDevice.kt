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
import java.net.Socket

private class GetLastSendingPhotoDate(private val sharedPreferences: ISharedPrefsRepository) :
    BaseUseCaseNoParams<Long>() {
    override operator fun invoke() =
        sharedPreferences.getLastSendingDate()
}

class GetPhotos(private val photoRepository: IPhotoRepository) :
    BaseUseCase<List<Photo>, Long>() {
    override operator fun invoke(params: Long) =
        photoRepository.getPhotosFromDevice(params)
}

private class SetLastSendingTime(private val sharedPreference: ISharedPrefsRepository) {
    operator fun invoke(date: Long) {
        sharedPreference.setLastSendingDate(date)
    }
}

private class CreateSocket(private val photoSenderSocketRepository: IPhotoSenderSocketRepository) {
    operator fun invoke() =
        photoSenderSocketRepository.createSocket()
}

private class Connect(private val photoSenderSocketRepository: IPhotoSenderSocketRepository) {
    operator fun invoke(host: String, port: Int, socket: Socket) =
        photoSenderSocketRepository.connect(host, port, socket)
}

private class SendPhoto(private val photoSenderSocketRepository: IPhotoSenderSocketRepository) {
    operator fun invoke(photo: Photo, albumName: String, socket: Socket) =
        photoSenderSocketRepository.sendPhotos(photo, albumName, socket)
}

private class Disconnect(private val photoSenderSocketRepository: IPhotoSenderSocketRepository) {
    operator fun invoke(socket: Socket) =
        photoSenderSocketRepository.disconnect(socket)
}

class SendNewPhotosToRemoteDevice(
    private val sharedPreference: ISharedPrefsRepository,
    private val photoRepository: IPhotoRepository,
    private val photoSenderSocketRepository: IPhotoSenderSocketRepository
) : BaseUseCase<Boolean, SendNewPhotosToRemoteDevice.SendNewPhotosParams>() {
    override fun invoke(
        params: SendNewPhotosParams): Result<Failure, Boolean> {
        val lastSendedPhotoDateResult = GetLastSendingPhotoDate(sharedPreference).invoke()
        if (lastSendedPhotoDateResult is Result.SUCCESS) {
            val lastSendedPhotoDate = lastSendedPhotoDateResult.data
            val getPhotosResult = GetPhotos(photoRepository).invoke(lastSendedPhotoDate)
            if (getPhotosResult is Result.SUCCESS) {
                val photosList = getPhotosResult.data
                if (photosList.isNullOrEmpty()) {
                    return Result.ERROR(UnknownError())
                }
                when (val socketResult = CreateSocket(photoSenderSocketRepository).invoke()) {
                    is Result.SUCCESS -> {
                        when (val connectionResult = Connect(photoSenderSocketRepository).invoke(
                            params.host, 4000, socketResult.data
                        )) {
                            is Result.SUCCESS -> {
                                for (photo in photosList) {
                                    when (val sendPhotoResult =
                                        SendPhoto(photoSenderSocketRepository).invoke(
                                            photo, params.albumName, socketResult.data
                                        )) {
                                        is Result.SUCCESS -> {
                                            SetLastSendingTime(sharedPreference).invoke(
                                                sendPhotoResult.data
                                            )
                                        }
                                        is Result.ERROR -> return Result.ERROR(sendPhotoResult.throwable)
                                    }

                                }
                                return when (val disconnectResult =
                                    Disconnect(photoSenderSocketRepository).invoke(socketResult.data)) {
                                    is Result.SUCCESS -> Result.SUCCESS(true)
                                    is Result.ERROR -> Result.ERROR(disconnectResult.throwable)
                                }
                            }
                            is Result.ERROR -> return Result.ERROR(connectionResult.throwable)
                        }
                    }
                    is Result.ERROR -> return Result.ERROR(socketResult.throwable)
                }
            }
        }
        return Result.ERROR(UnknownError())

    }

    class SendNewPhotosParams(
        val host: String,
        val albumName: String)
}

