package com.gawel.sender.di.modules

import android.content.Context
import com.gawel.sender.data.datasources.PhotosInPhoneMemoryDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
object PrepareAndSendPhotosModule {

    @Provides
    fun providePhotosInPhoneMemoryDataSource(@ApplicationContext context: Context) : PhotosInPhoneMemoryDataSource {
        return PhotosInPhoneMemoryDataSource(context = context)
    }
}