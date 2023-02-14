package com.example.cvpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.cvpro.databinding.ActivityHomeBinding
import com.example.cvpro.filtercams.BlurCameraActivity
import com.example.cvpro.filtercams.CartoonCameraActivity
import com.example.cvpro.filtercams.MainActivity

/**
 *   홈 화면 액티비티
 */
class HomeActivity : AppCompatActivity() {

    lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        // 버튼 리스너 세팅
        binding.blackCameraBt.setOnClickListener{ blackCamListener() }
        binding.cartoonCameraBt.setOnClickListener { cartoonCamListener() }
        binding.blurCameraBt.setOnClickListener { blurCamListener() }

        // 이미지 리스너 세팅
        binding.blackCamImg.setOnClickListener { blackCamListener() }
        binding.cartoonCameraBt.setOnClickListener { cartoonCamListener() }
        binding.blurCameraBt.setOnClickListener { blurCamListener() }
    }


    // 흑백 카메라 텍스트 클릭 리스너
    fun blackCamListener(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // 카툰 카메라 텍스트 클릭 리스너
    fun cartoonCamListener(){
        val intent = Intent(this, CartoonCameraActivity::class.java)
        startActivity(intent)
    }

    // 블러 카메라 텍스트 클릭 리스너
    fun blurCamListener(){
        val intent = Intent(this, BlurCameraActivity::class.java)
        startActivity(intent)
    }
}