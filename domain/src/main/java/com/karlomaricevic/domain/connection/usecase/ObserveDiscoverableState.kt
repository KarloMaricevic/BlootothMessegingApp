package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.DeviceDiscoverer

class ObserveDiscoverableState(
    private val discoverer: DeviceDiscoverer,
) {
    operator fun invoke() = discoverer.observeDiscoverableState()
}
