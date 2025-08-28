package com.karlomaricevic.domain.connection

import arrow.core.Either
import com.karlomaricevic.core.common.Failure
import com.karlomaricevic.domain.connection.models.Connection
import kotlinx.coroutines.flow.Flow

interface DeviceDiscoverer {
    suspend fun discoverAvailableConnections(): Either<Failure.ErrorMessage, List<Connection>>
    fun observeDiscoverableState(): Either<Failure.ErrorMessage, Flow<Boolean>>
}
