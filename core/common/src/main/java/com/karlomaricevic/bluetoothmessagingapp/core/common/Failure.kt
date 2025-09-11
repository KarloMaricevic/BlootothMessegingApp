package com.karlomaricevic.bluetoothmessagingapp.core.common

import arrow.core.Either.Left
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.Unknown

sealed interface Failure {
    data class ErrorMessage(val errorMessage: String) : Failure
    data object Unknown : Failure
}

fun Failure.foldToString(): String = when (this) {
    is ErrorMessage -> errorMessage
    is Unknown -> "Unknown error"
}

fun Exception.mapToLeft() = Left(ErrorMessage(this.message ?: this::class.java.name))
