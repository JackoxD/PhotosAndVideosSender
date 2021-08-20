package com.gawel.core.models

sealed class Result<Failure, out S> {

    data class SUCCESS<Failure, S>(val data: S) : Result<Failure, S>()
    data class ERROR<Failure, S>(val throwable: Failure) : Result<Failure, S>()
}