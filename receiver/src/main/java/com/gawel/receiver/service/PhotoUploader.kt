package com.gawel.receiver.service

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PhotoUploader"
class PhotoUploader {

    private var newPhotoDetails: ContentValues? = null
    private var contentUri: Uri? = null
    private var contentResolver: ContentResolver? = null

    fun clear() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            newPhotoDetails?.clear()
            newPhotoDetails?.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver?.update(contentUri!!, newPhotoDetails, null)
        }
    }

    fun getNewPhotoFile(context: Context, name: String, albumName: String): FileOutputStream {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver = context.contentResolver
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL_PRIMARY
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            newPhotoDetails = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + albumName)
//                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }


            contentUri = contentResolver!!.insert(
                collection, newPhotoDetails
            )
            Log.d(TAG, "getNewPhotoFile: " + contentUri?.encodedPath)

            return FileOutputStream(contentResolver!!.openFileDescriptor(contentUri!!, "w")?.fileDescriptor)
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            val albumFolder = File(imagesDir, albumName)
            if (!albumFolder.exists())
                albumFolder.mkdir()

            val image = File(imagesDir + File.separator + albumName,  name)
            return FileOutputStream(image)
        }

    }
}