package com.karlomaricevic.domain.core.models

import arrow.core.Either
import com.karlomaricevic.domain.core.models.Failure.ErrorMessage
import com.karlomaricevic.domain.core.models.Failure.Unknown

sealed interface Failure {
    data class ErrorMessage(val errorMessage: String) : Failure
    data object Unknown : Failure
}

fun Failure.foldToString(): String = when (this) {
    is ErrorMessage -> errorMessage
    is Unknown -> "Unknown error"
}

fun Exception.mapToLeft() = Either.Left(ErrorMessage(this.message ?: this::class.java.name))
