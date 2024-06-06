package com.karlom.bluetoothmessagingapp.data.bluetooth.models

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Job

class CommunicationEntry(
    val readingJob: Job,
    val socket: BluetoothSocket,
)
