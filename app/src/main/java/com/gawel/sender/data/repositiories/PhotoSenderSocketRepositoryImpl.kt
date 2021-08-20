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
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

private const val TAG = "PhotoSenderSocketReposi"

const val timeout = 2000
class PhotoSenderSocketRepositoryImpl(private val context: Context) : IPhotoSenderSocketRepository {

    private val connectTimeout = 4000


    private var connected = false

    override fun createSocket(): Result<Failure, Socket> {
        return try {
            Result.SUCCESS(Socket().apply {
                soTimeout = timeout
            })
        } catch (e: Exception) {
            Result.ERROR(UnknownError())
        }
    }

    override fun connect(host: String, port: Int, socket: Socket): Result<Failure, Boolean> {
        return try {
            socket.connect(InetSocketAddress(host, port), connectTimeout)
            onConnected()
            Result.SUCCESS(true)
        } catch (e: Exception) {
            Result.SUCCESS(false)

        }
    }

    override fun disconnect(socket: Socket): Result<Failure, Boolean> {
        return try {
            socket.close()
            onDisconnected()
            Result.SUCCESS(true)
        } catch (e: Exception) {
            Result.SUCCESS(false)
        }

    }

    override fun sendPhotos(photo: Photo, albumName: String, socket: Socket): Result<Failure, Long> {

        var lastSendedDate = 0L

            val uploadImage = uploadImage(photo, albumName, socket)
            if (uploadImage != null)
                lastSendedDate = uploadImage


        return if (lastSendedDate <= 0L)
            Result.ERROR(UnknownError())
        else
            Result.SUCCESS(lastSendedDate)

    }

    private fun uploadImage(photo: Photo, albumName: String, socket: Socket): Long? {
        // Open a specific media item using InputStream.
        val uri = photo.uri ?: throw DefaultException.UriCannotBeNull()
        val resolver = context.contentResolver
        resolver.openInputStream(uri).use { stream ->
            photo.size = stream!!.available().toLong()
            // Perform operations on "stream".
            if (connected) {
                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.write(byteArrayOf(0)) // send byte of file type
                dataOutputStream.writeUTF(photo.name) // send name of file with length of this string
                dataOutputStream.writeUTF(albumName) // send album name with length of this string
                dataOutputStream.writeLong(photo.size!!) // send file length
                socket.getOutputStream().write(stream.readBytes()) // send file
                return photo.dateTaken
            } else throw Exception("No connected")
        }
    }

    private fun onConnected() {
        connected = true
    }

    private fun onDisconnected() {
        connected = false
    }
}