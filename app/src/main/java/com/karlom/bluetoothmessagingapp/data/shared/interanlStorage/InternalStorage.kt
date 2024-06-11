package com.karlom.bluetoothmessagingapp.data.shared.interanlStorage

import android.content.Context
import android.net.Uri
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
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
                Either.Right(Uri.fromFile(file).toString())
            }
        } catch (e: FileNotFoundException) {
            Either.Left(Failure.ErrorMessage("Source image file not found"))
        }
    }
}
