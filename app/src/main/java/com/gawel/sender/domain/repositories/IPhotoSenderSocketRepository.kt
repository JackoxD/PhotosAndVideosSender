package com.gawel.sender.domain.repositories

import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.sender.domain.models.Photo

interface IPhotoSenderSocketRepository {

    suspend fun sendPhotos(photos: List<Photo>) : Result<Failure, Long>
}