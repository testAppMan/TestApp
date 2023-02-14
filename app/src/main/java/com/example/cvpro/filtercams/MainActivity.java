package com.example.cvpro.filtercams;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.cvpro.HomeActivity;
import com.example.cvpro.R;
import com.example.cvpro.utils.MatConverter;
import com.example.cvpro.utils.PhotoSaveUtils;

import android.content.DialogInterface;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import java.util.Collections;
import java.util.List;
import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImageView shootingImageBt;
    private ImageView backBt;
    // 유틸 클래스 프로퍼티
    private MatConverter matConverter;
    private PhotoSaveUtils photoSaveUtils;
    // 캡처에 사용할 mat 객체 (mat -> bitmap)
    private Mat captureMat;
    // 사진을 저장할 때 쓸 빈 bitmap 객체
    private Bitmap saveBitmap;
    private Context context;
    // 퍼미션 권한 코드
    private static final int STORAGE_PERMISSION_CODE = 108;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    // 네이티브 메소드 먼저 로드
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult, long matAddrtCapture);


    // 해당 콜백이 호출되기 전, opencv 호출이나 opencv 종속 라이브러리 로드 불가능
    // 즉, 이 메서드로 성공적으로 opencv 초기화 후, opencv 고유 라이브러리를 호출하는 것
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };



    /**
     *
     *   Activity Lifecycle 함수들
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_camera);

        // 뷰 및 각종 변수 초기화
        initialize();

        // 클릭리스너 세팅
        shootingImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureMat != null){
                    externalStoragePermissionCheck();
                    // 현재 resultMat -> bitmap
                    saveBitmap = matConverter.convertMatToBitmap(captureMat);
                    // 외부저장소 갤러리에 저장
                    photoSaveUtils.saveBitmapToGallery(saveBitmap, context);
                }
            }
        });

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtListerner();
            }
        });

        // full screen 상태를 유지하고 status bar 등 불필요한 요소 배제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_blur_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
    }

    @Override
    protected void onStart() {
        super.onStart();
        // onResume 진입 전 권한 체크
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // 메모리 해제
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();

        // 메모리 해제
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    /**
     *   Opencv 관련 코드들
     *
     */

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    // 프레임에 필터 적용하는 파트 + 캡처시 사용할 Mat 처리
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult == null) {
            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        }
        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr(), captureMat.getNativeObjAddr());
        return matResult;
    }

    // 카메라와 OPNECV 라이브러리간 조율 담당
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


    /**
     *     Permission 관련 코드들 / 카메라
     */
    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }



    // 퍼미션 알람시 보여줄 다이얼로그 처리
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    /**
     *     Permission 관련 코드들 / 외부저장소
     */

    private void externalStoragePermissionCheck(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);

            } else {

            }
        }
    }

    /**
     *    Permission 관련 코드 / 퍼미션 처리
     */

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 카메라 퍼미션
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }
        //외부저장소 퍼미션
        else if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length >0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     *    프로퍼티 초기화 함수
     */
    private void initialize(){
        shootingImageBt = findViewById(R.id.shooting_cameraImg_bt);
        matConverter = new MatConverter();
        photoSaveUtils = new PhotoSaveUtils();
        saveBitmap = null;
        context = this;
        captureMat = new Mat();
        backBt = findViewById(R.id.back_bt);
    }

    /**
     *   Listeners
     */
    private void backBtListerner(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}