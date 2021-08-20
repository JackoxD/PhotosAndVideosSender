package com.gawel.sender.di.modules

import android.content.Context
import com.gawel.sender.data.datasources.PhotosInPhoneMemoryDataSource
import com.gawel.sender.data.datasources.SharedPrefsDataSource
import com.gawel.sender.data.repositiories.PhotoSenderSocketRepositoryImpl
import com.gawel.sender.data.repositiories.PhotosRepositoryImpl
import com.gawel.sender.data.repositiories.SharedPrefsRepositoryImpl
import com.gawel.sender.domain.repositories.IPhotoRepository
import com.gawel.sender.domain.repositories.IPhotoSenderSocketRepository
import com.gawel.sender.domain.repositories.ISharedPrefsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrepareAndSendPhotosModule {

    @Provides
    @Singleton
    fun providePhotosInPhoneMemoryDataSource(@ApplicationContext context: Context) : PhotosInPhoneMemoryDataSource {
        return PhotosInPhoneMemoryDataSource(context = context)
    }

    @Provides
    @Singleton
    fun provideSharedPrefsDataSource(@ApplicationContext context: Context) : SharedPrefsDataSource {
        return SharedPrefsDataSource(context)
    }

    @Provides
    @Singleton
    fun providePhotosRepositoryImpl(photosInPhoneMemoryDataSource: PhotosInPhoneMemoryDataSource): IPhotoRepository {
        return PhotosRepositoryImpl(photosInPhoneMemoryDataSource = photosInPhoneMemoryDataSource)
    }

    @Provides
    @Singleton
    fun providePrefsRepository(sharedPrefsDataSource: SharedPrefsDataSource) : ISharedPrefsRepository {
        return SharedPrefsRepositoryImpl(sharedPrefsDataSource = sharedPrefsDataSource)
    }

    @Provides
    @Singleton
    fun providePhotosSenderRepository(@ApplicationContext context: Context) : IPhotoSenderSocketRepository {
        return PhotoSenderSocketRepositoryImpl(context)
    }

}