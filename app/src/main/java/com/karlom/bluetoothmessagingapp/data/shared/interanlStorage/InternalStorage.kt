package com.karlom.bluetoothmessagingapp.data.shared.interanlStorage

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
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

    private companion object {
        const val NO_COLUMN = -1
    }

    fun saveImage(srcUri: String, destName: String): Either<Failure.ErrorMessage, String> {
        val uri = Uri.parse(srcUri)
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Either.Left(Failure.ErrorMessage("Provider has crashed"))
            } else {
                val file = File(context.filesDir, destName)
                FileOutputStream(file).use { output -> inputStream.copyTo(output) }
                Either.Right(Uri.fromFile(file).toString())
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
            val inputStream = context.contentResolver.openInputStream(uri.toUri())
                ?: return Either.Left(Failure.ErrorMessage("Content provider error"))
            Either.Right(inputStream)
        } catch (e: IOException) {
            Either.Left(Failure.ErrorMessage("File not found"))
        }
    }


    fun getFileSize(uri: String): Either<Failure.ErrorMessage, Long> {
        val projection = arrayOf(MediaStore.MediaColumns.SIZE)
        val cursor = context.contentResolver.query(uri.toUri(), projection, null, null, null)
            ?: return Either.Left(Failure.ErrorMessage("Content provider error"))
        return if (cursor.moveToFirst()) {
            val sizeColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
            if (sizeColumnIndex == NO_COLUMN) {
                cursor.close()
                Either.Left(Failure.ErrorMessage("Cant find size column"))
            } else {
                val fileSize = cursor.getLong(sizeColumnIndex)
                cursor.close()
                Either.Right(fileSize)
            }
        } else {
            cursor.close()
            Either.Left(Failure.ErrorMessage("Cursor of a file is empty"))
        }
    }
}
