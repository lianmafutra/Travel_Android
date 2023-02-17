package com.app.travel.utils
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.io.*

class GetFilePath {
    companion object{
        fun createCopyAndReturnRealPath(
            context: Context, uri: Uri?
        ): String? {
            val contentResolver: ContentResolver = context.contentResolver
            val filename: String?
            val returnCursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null)!!
            val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            filename = returnCursor.getString(nameIndex)
            returnCursor.close()
            val filePath: String =
                context.applicationInfo.dataDir + File.separator.toString() + filename
            val file = File(filePath)
            try {
                val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                outputStream.close()
                inputStream.close()
            } catch (ignore: IOException) {
                return null
            }
            return file.path
        }
    }
}