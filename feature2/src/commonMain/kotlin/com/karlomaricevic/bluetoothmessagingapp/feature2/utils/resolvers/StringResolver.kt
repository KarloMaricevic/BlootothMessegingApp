package com.karlomaricevic.bluetoothmessagingapp.feature2.utils.resolvers

interface StringResolver<T> {
    fun getString(identifier: T): String
}
