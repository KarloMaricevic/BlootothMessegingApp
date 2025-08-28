package com.karlomaricevic.platform.utils

import android.content.Context
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.karlomaricevic.core.common.Failure.ErrorMessage
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.io.copyTo

class FileStorage(private val context: Context) {

    fun saveImage(filePath: String, destName: String): Either<ErrorMessage, String> {
        val uri = filePath.toUri()
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                ErrorMessage("Provider has crashed").left()
            } else {
                val file = File(context.filesDir, destName)
                FileOutputStream(file).use { output -> inputStream.copyTo(output) }
                file.absolutePath.right()
            }
        } catch (_: FileNotFoundException) {
            ErrorMessage("Source image file not found").left()
        }
    }

    fun save(byteArray: ByteArray, destName: String) = try {
        val file = File(context.filesDir, destName)
        file.parentFile?.mkdirs()
        context.openFileOutput(destName, Context.MODE_PRIVATE).use {
            it.write(byteArray)
            it.close()
        }
        file.absolutePath.right()
    } catch (e: IOException) {
        ErrorMessage(e.message ?: "IOException").left()
    }

    fun getFileInputStream(filePath: String) = try {
        val inputStream = File(filePath).inputStream()
        inputStream.right()
    } catch (_: IOException) {
        ErrorMessage("File not found").left()
    }

    fun getFileSize(filePath: String) = try {
        File(filePath).length().right()
    } catch (e: Exception) {
        ErrorMessage("Error reading file length").left()
    }

    fun createEmptyFile(filePath: String) = save(byteArrayOf(), filePath)

    fun deleteFile(filePath: String) = try {
        val file = File(context.filesDir, filePath)
        if (file.delete()) {
            Either.Right(Unit)
        } else {
            ErrorMessage("Cant delete file").left()
        }
    } catch (e: SecurityException) {
        ErrorMessage(e.message ?: "Cant access file").left()
    }
}
