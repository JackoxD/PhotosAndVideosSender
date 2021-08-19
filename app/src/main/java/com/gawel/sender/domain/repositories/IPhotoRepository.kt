package com.gawel.sender.domain.repositories

import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.sender.domain.models.Photo


interface IPhotoRepository {
    suspend fun getPhotosFromDevice(fromDate: Long): Result<Failure, List<Photo>>

    suspend fun put(photo: Photo)
}