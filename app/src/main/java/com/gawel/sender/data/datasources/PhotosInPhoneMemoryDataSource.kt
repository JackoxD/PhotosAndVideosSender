package com.gawel.sender.data.datasources

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.gawel.sender.data.models.PhotoDevice

private const val TAG = "PhotosInPhoneMemoryData"
class PhotosInPhoneMemoryDataSource(private val context: Context) {

    fun getPhotosFromDevice(fromDate: Long): List<PhotoDevice> {

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(
            fromDate.toString()
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} ASC"

        val photosList = mutableListOf<PhotoDevice>()
        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)

                Log.d(TAG, "doWork: Photo:\nName: $name\ndateTaken: $dateTaken")
                var contentUri: Uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentUri = MediaStore.setRequireOriginal(contentUri)
                }

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                val photo = PhotoDevice(contentUri, dateTaken, name, null)
                photosList.add(photo)
            }
        }
        return photosList
    }

    fun put(photo: PhotoDevice) {
        TODO("Not yet implemented")
    }
}