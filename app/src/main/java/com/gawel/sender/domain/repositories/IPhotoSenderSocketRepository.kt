package com.gawel.sender.domain.repositories

import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.sender.domain.models.Photo
import java.net.Socket

interface IPhotoSenderSocketRepository {

    fun createSocket() : Result<Failure, Socket>

    fun connect(host: String, port: Int, socket: Socket) : Result<Failure, Boolean>

    fun sendPhotos(photo: Photo, albumName: String, socket: Socket) : Result<Failure, Long>

    fun disconnect(socket: Socket) : Result<Failure, Boolean>


}