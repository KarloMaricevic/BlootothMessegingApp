package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer

class ObserveDiscoverableState(
    private val discoverer: DeviceDiscoverer,
) {
    operator fun invoke() = discoverer.observeDiscoverableState()
}
