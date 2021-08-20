package com.gawel.sender.services

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.model.Dependency
import com.gawel.core.models.Result
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.gawel.sender.domain.repositories.IPhotoSenderSocketRepository
import com.gawel.sender.domain.repositories.ISharedPrefsRepository
import com.gawel.sender.domain.usecases.SendNewPhotosToRemoteDevice
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val TAG = "SenderService"
const val INPUT_HOST = "host"
const val INPUT_ALBUM_NAME = "album_name"

@HiltWorker
class SenderWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    sharedPrefsRepository: ISharedPrefsRepository,
    photosRepository: IPhotoRepository,
    senderRepository: IPhotoSenderSocketRepository
) :
    Worker(appContext, workerParams) {

    private val useCase = SendNewPhotosToRemoteDevice(
        sharedPrefsRepository,
        photosRepository,
        senderRepository
    )


    override fun doWork(): Result {
        val host = inputData.getString(INPUT_HOST) ?: throw Throwable("Host cannot be null")
        val albumName =
            inputData.getString(INPUT_ALBUM_NAME) ?: throw Throwable("Album name shouldn't be null")



        return when (
            useCase.invoke(
                SendNewPhotosToRemoteDevice.SendNewPhotosParams(
                    host,
                    albumName
                )
            )) {
            is com.gawel.core.models.Result.SUCCESS -> Result.success()
            is com.gawel.core.models.Result.ERROR -> Result.failure()
        }


    }

}