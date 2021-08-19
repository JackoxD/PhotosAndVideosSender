package com.gawel.sender.data.models

import android.net.Uri
import com.gawel.sender.data.mappers.DeviceMemoryObjectMapper


data class PhotoDevice(val uri: Uri?, val dateTaken: Long, val name: String, var size: Long?) {

    fun toDomainModel() = DeviceMemoryObjectMapper().mapFromEntity(this)
}