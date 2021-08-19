package com.gawel.sender.data.repositiories

import android.content.Context
import android.util.Log
import com.gawel.core.exceptions.DefaultException
import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.core.models.UnknownError
import com.gawel.sender.domain.models.Photo
import com.gawel.sender.domain.repositories.IPhotoSenderSocketRepository
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val TAG = "PhotoSenderSocketReposi"

class PhotoSenderSocketRepositoryImpl(
    private val host: String,
    private val albumName: String,
    private val context: Context
) : IPhotoSenderSocketRepository {

    private val port = 4000
    private val soTimeout = 2000
    private val connectTimeout = 4000

    private lateinit var socket: Socket

    private var connected = false

    override suspend fun sendPhotos(photos: List<Photo>): Result<Failure, Long> {

        socket = Socket()
        socket.soTimeout = this.soTimeout

        var lastSendedDate = 0L

        for (photo in photos) {
            val uploadImage = uploadImage(photo, host, albumName)
            if (uploadImage != null)
                lastSendedDate = uploadImage
        }


        return if (lastSendedDate <= 0L)
            Result.ERROR(UnknownError())
        else
            Result.SUCCESS(lastSendedDate)

    }

    private fun uploadImage(photo: Photo, host: String, albumName: String): Long? {
        // Open a specific media item using InputStream.
        val uri = photo.uri ?: throw DefaultException.UriCannotBeNull()
        val resolver = context.contentResolver
        resolver.openInputStream(uri).use { stream ->
            photo.size = stream!!.available().toLong()
            // Perform operations on "stream".
            if (!connected) {
                try {
                    socket.connect(InetSocketAddress(host, port), connectTimeout)
                    onConnected()
                } catch (e: Throwable) {
                    Log.e(TAG, "uploadImage: ", e)
                    return null
                }
            }
            if (connected) {
                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.write(byteArrayOf(0)) // send byte of file type
                dataOutputStream.writeUTF(photo.name) // send name of file with length of this string
                dataOutputStream.writeUTF(albumName) // send album name with length of this string
                dataOutputStream.writeLong(photo.size!!) // send file length
                socket.getOutputStream().write(stream.readBytes()) // send file
                return photo.dateTaken
            }
        }
        return null;
    }

    private fun onConnected() {
        connected = true
    }

    private fun onDisconnected() {
        connected = false
    }
}