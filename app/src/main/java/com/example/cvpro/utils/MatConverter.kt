package com.example.cvpro.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 *   opencv mat 객체를 bitmap으로 저장하기 위한 유틸 클래스
 */
class MatConverter {


    // mat 객체를 bitmap 객체로 전환해주는 함수
    fun convertMatToBitmap(mat: Mat): Bitmap? {
        var bitmap : Bitmap? = null
        val rgb = Mat()
        Imgproc.cvtColor(mat, rgb, Imgproc.COLOR_BGR2RGB)

        try {
            bitmap = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(rgb, bitmap)
            bitmap = rotateBitmap(bitmap)
        }
        catch (e:Exception){
            Log.d("MatConverter : Error", "mat 객체 bitmap 변환 실패 : ${e}")
        }
        return bitmap
    }


    // bitmap 객체를 정방향으로 회전시키기 위한 함수
    private fun rotateBitmap(bitmap: Bitmap): Bitmap{

        val matrix = Matrix()
        matrix.postRotate(90.0F)

        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return rotatedBitmap
    }
}