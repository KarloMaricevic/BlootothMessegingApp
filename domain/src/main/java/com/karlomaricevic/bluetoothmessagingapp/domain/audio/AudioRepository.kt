package com.karlomaricevic.bluetoothmessagingapp.domain.audio

import arrow.core.Either
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure

interface AudioRepository {
    fun deleteAudio(path: String): Either<Failure.ErrorMessage, Unit>
}
