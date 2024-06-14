package com.karlom.bluetoothmessagingapp.data.shared.interanlStorage

import android.content.Context
import android.net.Uri
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class InternalStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun saveImage(srcUri: String, destName: String): Either<Failure.ErrorMessage, String> {
        val uri = Uri.parse(srcUri)
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Either.Left(Failure.ErrorMessage("Provider has crashed"))
            } else {
                val file = File(context.filesDir, destName)
                FileOutputStream(file).use { output -> inputStream.copyTo(output) }
                Either.Right(file.absolutePath)
            }
        } catch (e: FileNotFoundException) {
            Either.Left(Failure.ErrorMessage("Source image file not found"))
        }
    }

    fun save(byteArray: ByteArray, destName: String): Either<Failure.ErrorMessage, String> = try {
        val file = File(context.filesDir, destName)
        file.parentFile?.mkdirs()
        context.openFileOutput(destName, Context.MODE_PRIVATE).use {
            it.write(byteArray)
            it.close()
        }
        Either.Right(file.absolutePath)
    } catch (e: IOException) {
        Either.Left(Failure.ErrorMessage(e.message ?: "IOException"))
    }

    fun getFileInputStream(uri: String): Either<Failure.ErrorMessage, InputStream> {
        return try {
            val inputStream = File(uri).inputStream()
            Either.Right(inputStream)
        } catch (e: IOException) {
            Either.Left(Failure.ErrorMessage("File not found"))
        }
    }

    fun getFileSize(uri: String): Either<Failure.ErrorMessage, Long> = try {
        Either.Right(File(uri).length())
    } catch (e: Exception) {
        Either.Left(Failure.ErrorMessage("Error reading file length"))
    }

    fun createEmptyFile(uri: String) = save(byteArrayOf(), uri)
}
