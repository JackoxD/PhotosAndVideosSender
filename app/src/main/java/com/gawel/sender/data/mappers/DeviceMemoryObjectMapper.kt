package com.gawel.sender.data.mappers

import com.gawel.core.mappers.EntityMapper
import com.gawel.sender.data.models.PhotoDevice
import com.gawel.sender.domain.models.Photo

class DeviceMemoryObjectMapper: EntityMapper<PhotoDevice, Photo> {
    override fun mapFromEntity(entity: PhotoDevice): Photo {
        return Photo(
            uri = entity.uri,
            dateTaken = entity.dateTaken,
            name = entity.name,
            size = entity.size
        )
    }

    override fun mapToEntity(domainModel: Photo): PhotoDevice {
        return PhotoDevice(
            uri = domainModel.uri,
            dateTaken = domainModel.dateTaken,
            name = domainModel.name,
            size = domainModel.size
        )
    }

}