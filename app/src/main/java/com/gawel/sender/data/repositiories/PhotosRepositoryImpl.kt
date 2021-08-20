package com.gawel.sender.data.repositiories

import com.gawel.core.models.Failure
import com.gawel.core.models.ReadDeviceMemoryFailure
import com.gawel.core.models.Result
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.gawel.sender.data.datasources.PhotosInPhoneMemoryDataSource
import com.gawel.sender.domain.models.Photo
import java.lang.Exception

private const val TAG = "DirectoryPhotosDataSour"

class PhotosRepositoryImpl(private val photosInPhoneMemoryDataSource: PhotosInPhoneMemoryDataSource) :
    IPhotoRepository {

    override fun getPhotosFromDevice(fromDate: Long): Result<Failure, List<Photo>> {
        return try {
            Result.SUCCESS(
                photosInPhoneMemoryDataSource.getPhotosFromDevice(fromDate)
                    .map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.ERROR(ReadDeviceMemoryFailure())
        }

    }

    override fun put(photo: Photo) {
        photosInPhoneMemoryDataSource.put(
            photo
                .fromDomainModel()
        )
    }
}