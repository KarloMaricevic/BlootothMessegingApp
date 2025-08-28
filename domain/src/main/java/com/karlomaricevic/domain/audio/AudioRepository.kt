package com.karlomaricevic.domain.audio

import arrow.core.Either
import com.karlomaricevic.core.common.Failure

interface AudioRepository {
    fun deleteAudio(path: String): Either<Failure.ErrorMessage, Unit>
}
