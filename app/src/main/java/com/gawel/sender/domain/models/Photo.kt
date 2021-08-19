package com.gawel.sender.domain.models

import android.net.Uri
import com.gawel.sender.data.mappers.DeviceMemoryObjectMapper


const val photoByte: Byte = 0
const val videoByte: Byte = 1

data class Photo(val uri: Uri?, val dateTaken: Long, val name: String, var size: Long?) {

    fun fromDomainModel() = DeviceMemoryObjectMapper().mapToEntity(this)

    constructor() : this(Uri.EMPTY, 0L, "name", null)

}
