package com.gawel.core.usecases

import com.gawel.core.models.Failure
import com.gawel.core.models.Result

abstract class BaseUseCase<out Type, in Params> where Type : Any {

    abstract suspend operator fun invoke(params: Params): Result<Failure, Type>

}
abstract class BaseUseCaseNoParams<out Type> where Type : Any {

    abstract suspend operator fun invoke(): Result<Failure, Type>

}