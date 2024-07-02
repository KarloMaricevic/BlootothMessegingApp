package com.karlom.bluetoothmessagingapp.core.models

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.core.models.Failure.Unknown

sealed interface Failure {
    data class ErrorMessage(val errorMessage: String) : Failure
    data object Unknown : Failure
}

fun Failure.foldToString(): String = when (this) {
    is ErrorMessage -> errorMessage
    is Unknown -> "Unknown error"
}

fun Exception.mapToLeft() = Either.Left(ErrorMessage(this.message ?: this::class.java.name))
