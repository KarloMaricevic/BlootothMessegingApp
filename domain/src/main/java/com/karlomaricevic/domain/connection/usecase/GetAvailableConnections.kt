package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.DeviceDiscoverer

class GetAvailableConnections(
    private val discoverer: DeviceDiscoverer,
) {

    suspend operator fun invoke() = discoverer.discoverAvailableConnections()
}
