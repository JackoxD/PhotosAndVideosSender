package com.gawel.core.exceptions

import java.lang.Exception

open class DefaultException(override val message: String?) : Exception() {

    class UriCannotBeNull() : DefaultException(message = "Uri cannot be null")


}