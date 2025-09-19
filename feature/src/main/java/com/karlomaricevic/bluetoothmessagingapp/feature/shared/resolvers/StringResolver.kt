package com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers

interface StringResolver<T> {
    fun getString(identifier: T): String
}
