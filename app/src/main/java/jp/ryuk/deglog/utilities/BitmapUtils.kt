package jp.ryuk.deglog.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.json.JSONArray
import java.io.ByteArrayOutputStream

object BitmapUtils {

    // URIをBitmapに変換と同時にリサイズ
    fun createBitmap(context: Context, uri: Uri): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            else -> {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }

        return resizeBitmap(bitmap)
    }

    // Bitmapをリサイズ w100 h100
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val oldWidth = bitmap.width
        val oldHeight = bitmap.height
        if (oldWidth < 100 && oldHeight < 100) return bitmap

        val scaleWidth = 100.toFloat() / oldWidth
        val scaleHeight = 100.toFloat() / oldHeight
        val scaleFactor = scaleWidth.coerceAtMost(scaleHeight)
        val scale = Matrix()
        scale.postScale(scaleFactor, scaleFactor)
        val resizeBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false)
        bitmap.recycle()
        return resizeBitmap
    }

    // BitmapをJsonStringに変換
    fun convertBitmapToJsonString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return JSONArray(stream.toByteArray()).toString()
    }

    // JsonStringをBitmapに変換
    fun convertJsonToBitmap(jsonString: String): Bitmap {
        val jsonArray = JSONArray(jsonString)

        val bytes = mutableListOf<Byte>().apply {
            for (i in 0 until jsonArray.length()) {
                add(jsonArray.get(i).toString().toByte())
            }
        }
        val byteArray = bytes.toByteArray()

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}