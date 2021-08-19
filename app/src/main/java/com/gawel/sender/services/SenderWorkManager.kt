package com.gawel.sender.services

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gawel.sender.domain.usecases.SendNewPhotosToRemoteDevice
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val TAG = "SenderService"
const val INPUT_HOST = "host"
const val INPUT_ALBUM_NAME = "album_name"

class SenderWorkManager(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private val port = 4000
    private val soTimeout = 2000
    private val connectTimeout = 4000

    private lateinit var socket: Socket

    private var connected = false

    override fun doWork(): Result {
        TODO()
        /*SendNewPhotosToRemoteDevice(

        ).invoke()*/
        return Result.success()
    }

}