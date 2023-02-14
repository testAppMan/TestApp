package com.example.cvpro.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

/**
 *   bitmap 파일을 기기 혹은 앱 내부에 저장하기 위한 클래스
 */
class PhotoSaveUtils {

    // bitmap 객체를 jpg로 저장하는 함수
    fun saveBitmapToGallery(bitmap: Bitmap, context: Context){

        val root: String = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
        val dir: File = File(root)
        var out: FileOutputStream? = null
        dir.mkdirs()

        val fileName = "Image-${System.currentTimeMillis()}.jpg"
        val file = File(dir, fileName)
        if (file.exists()) file.delete()

        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)

            // 새로 media file이 갱신되었음을 알리는 함수 (scan)
            MediaScannerConnection.scanFile(context, arrayOf(file.getAbsolutePath()), arrayOf("image/jpeg"), null)
        } catch (e:Exception){
            e.printStackTrace()
        } finally {
            // 스트림 닫아주기
            out?.flush()
            out?.close()
        }
    }
}