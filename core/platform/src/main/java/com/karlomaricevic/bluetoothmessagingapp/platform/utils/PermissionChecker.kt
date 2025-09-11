package com.karlomaricevic.bluetoothmessagingapp.platform.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat.checkSelfPermission
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage

class PermissionChecker(private val context: Context) {

    private fun hasPermissionForBluetoothConnect(): Either<ErrorMessage, Boolean> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Right(
                checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT,
                ) == PERMISSION_GRANTED
            )
        } else {
            Left(ErrorMessage("BluetoothConnect permission requires SDK >= 31"))
        }

    private fun hasPermissionForLocalMacAddress() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Right(
                checkSelfPermission(
                    context,
                    "android.permission.LOCAL_MAC_ADDRESS",
                ) == PERMISSION_GRANTED
            )
        } else {
            Left(ErrorMessage("AccessToMacAddress permission requires SKD >= 31"))
        }

    private fun hasBluetoothPermission() =
        checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PERMISSION_GRANTED

    fun hasAccessToBluetoothMacAddress() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermissionForLocalMacAddress().onRight { hasPermissionForBluetoothConnect() }.fold(
                ifLeft =  { false },
                ifRight = { hasPermission -> hasPermission },
            )
        } else {
            hasBluetoothPermission()
        }

    fun hasPermissionToStartOrConnectToBtServer() = checkSelfPermission(
        context,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            Manifest.permission.BLUETOOTH
        },
    ) == PERMISSION_GRANTED
}