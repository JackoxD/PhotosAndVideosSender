package com.gawel.sender.presentation.mainScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import java.net.DatagramPacket
import java.net.DatagramSocket
import androidx.lifecycle.MutableLiveData
import com.gawel.sender.services.INPUT_ALBUM_NAME
import com.gawel.sender.services.INPUT_HOST
import com.gawel.sender.services.SenderWorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _ipsAdresses = MutableLiveData<String>()
    val ipsAdresses : LiveData<String> = _ipsAdresses

    private lateinit var uploadPhotosWorker: WorkRequest

    init {
        val thread = Job()
        CoroutineScope(IO + thread).launch {
            searchForBroadcasts()
        }
    }

    fun requestWorkerManager(host: String, albumName: String) {
        Log.d(TAG, "requestWorkerManager: called.")
        uploadPhotosWorker =
            OneTimeWorkRequestBuilder<SenderWorkManager>()
                .setInputData(
                    workDataOf(
                        INPUT_HOST to host,
                        INPUT_ALBUM_NAME to albumName
                    )
                )
                .build()

        WorkManager
            .getInstance(getApplication())
            // define  https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work
            .enqueue(uploadPhotosWorker)
    }

    fun searchForBroadcasts() {
        Log.d(TAG, "searchForBroadcasts: called.")
        val text: String
        val serverPort = 3999
        val message = ByteArray(2048)
        val p = DatagramPacket(message, message.size)
        val s = DatagramSocket(serverPort)
        s.receive(p); // blocks until something is received
        text = String(message, 0, p.length)
        Log.d(TAG, "searchForBroadcasts: received: $text")
        _ipsAdresses.postValue(text)

        s.close()
    }
}