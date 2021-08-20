package com.gawel.sender.domain.repositories

import com.gawel.core.models.Failure
import com.gawel.core.models.Result

interface ISharedPrefsRepository {

    fun getLastSendingDate() : Result<Failure, Long>;

    fun setLastSendingDate(date: Long);

}