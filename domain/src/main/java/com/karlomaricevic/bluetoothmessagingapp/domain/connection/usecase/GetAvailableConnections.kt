package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer

class GetAvailableConnections(
    private val discoverer: DeviceDiscoverer,
) {

    suspend operator fun invoke() = discoverer.discoverAvailableConnections()
}
