package com.gawel.sender.data.repositiories

import android.util.Log
import com.gawel.core.models.Failure
import com.gawel.core.models.Result
import com.gawel.core.models.SharedPreferencesReadFailure
import com.gawel.sender.data.datasources.SharedPrefsDataSource
import com.gawel.sender.domain.repositories.ISharedPrefsRepository
import java.lang.Exception

private const val TAG = "SharedPrefsRepositoryIm"
class SharedPrefsRepositoryImpl(private val sharedPrefsDataSource: SharedPrefsDataSource) : ISharedPrefsRepository {
    override suspend fun getLastSendingDate(): Result<Failure, Long> {
        return try {
            Result.SUCCESS(sharedPrefsDataSource.getLastSendingDate())
        } catch (e: Exception) {
            Log.e(TAG, "getLastSendingDate: ", e)
            Result.ERROR(SharedPreferencesReadFailure())
        }
    }

    override suspend fun setLastSendingDate(date: Long) {
        try {
            sharedPrefsDataSource.setLastSendingDate(date)
        } catch (e: Exception) {
            Log.e(TAG, "setLastSendingDate: ", e)
        }
    }
}