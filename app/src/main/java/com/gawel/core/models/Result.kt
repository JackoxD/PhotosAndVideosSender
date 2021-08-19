package com.gawel.core.models

open class Result<Failure, out S> {

    data class SUCCESS<S>(val data: S) : Result<Failure, S>() {
    }
    data class ERROR<S>(val throwable: Failure) : Result<Failure, S>()
}