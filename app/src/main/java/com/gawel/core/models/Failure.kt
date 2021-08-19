package com.gawel.core.models

abstract class Failure(private val additionalMsg: String = "") {
    abstract val msg : String

    fun getMessage() = "$msg\n$additionalMsg"
}

open class ReadDeviceMemoryFailure(additionalMsg: String = "") : Failure(additionalMsg) {
    override val msg = "Błąd odczytu danych z pamięci urządzenia."
}

open class SharedPreferencesFailure(additionalMsg: String = "") : Failure(additionalMsg) {
    override val msg = "Błąd przy komunikacji z plikami aplikacji."
}

class SharedPreferencesWriteFailure(additionalMsg: String = "") : SharedPreferencesFailure(additionalMsg) {
    override val msg = "Błąd przy próbie zapisu danych do pliku."
}

class SharedPreferencesReadFailure(additionalMsg: String = "") : SharedPreferencesFailure(additionalMsg) {
    override val msg = "Błąd przy próbie odczytu danych do pliku."
}

class UnknownError(additionalMsg: String = "") : Failure(additionalMsg) {
    override val msg = "Nieznany błąd."
}