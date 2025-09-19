package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers

import android.R.attr.name
import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenStringKeys.*
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

data class AddDeviceStringsResolver(private val context: Context): StringResolver<AddDeviceScreenStringKeys> {

    private val stringMap = mapOf(
        MAKE_DEVICE_VISIBLE_MESSAGE to context.getString(R.string.add_device_screen_make_device_visible_message),
        BLUETOOTH_DEVICES_TITLE to context.getString(R.string.add_device_screen_bluetooth_devices_title),
        START_SEARCH_BUTTON to context.getString(R.string.add_device_screen_start_search_button),
        NO_DEVICES_NEARBY to context.getString(R.string.bluetooth_device_screen_no_devices_nearby),
        BACK_CONTENT_DESCRIPTION to context.getString(R.string.default_icon_content_description),
        DEFAULT_ICON_CONTENT_DESCRIPTION to context.getString(R.string.default_icon_content_description),
        MAKE_DEVICE_VISIBLE_ERROR_TITLE to context.getString(R.string.add_device_screen_make_device_visible_error_title),
        MAKE_DEVICE_VISIBLE_ERROR_MESSAGE to context.getString(R.string.add_device_screen_make_device_visible_error_message),
        CONNECTING_TO_DEVICE_ERROR_TITLE to context.getString(R.string.add_device_screen_connecting_to_device_error_title),
        CONNECTING_TO_DEVICE_ERROR_MESSAGE to context.getString(R.string.add_device_screen_connecting_to_device_error_message),
        DIALOG_CONFIRM_BUTTON to context.getString(R.string.dialog_confirm_button),
    )

    override fun getString(identifier: AddDeviceScreenStringKeys): String {
        return stringMap[identifier] ?: error("String not found: $name")
    }
}