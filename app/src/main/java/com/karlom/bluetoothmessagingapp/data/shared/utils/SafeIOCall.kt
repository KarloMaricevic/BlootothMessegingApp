package com.karlom.bluetoothmessagingapp.data.shared.utils

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.models.mapToLeft

suspend fun <T> safeIOCall(call: suspend () -> T): Either<Failure.ErrorMessage,T> =
    try { Either.Right(call()) } catch (e: Exception) { e.mapToLeft() }
