package com.gawel.sender.domain.repositories

import com.gawel.core.models.Failure
import com.gawel.core.models.Result

interface ISharedPrefsRepository {

    suspend fun getLastSendingDate() : Result<Failure, Long>;

    suspend fun setLastSendingDate(date: Long);

}