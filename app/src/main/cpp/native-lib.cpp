#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;


extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_CartoonCameraActivity_ConvertRGBtoCartoon(JNIEnv *env, jobject thiz,
                                                                 jlong mat_addr_input,
                                                                 jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoCartoon()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_CartoonCameraActivity_ConvertRGBtoCartoonTwo(JNIEnv *env, jobject thiz,
                                                                    jlong mat_addr_input,
                                                                    jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoCartoonTwo()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

    GaussianBlur(matInput, matResult, Size(7,7), 0);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_CartoonCameraActivity_ConvertRGBtoCartoonThree(JNIEnv *env, jobject thiz,
                                                                      jlong mat_addr_input,
                                                                      jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoCartoonThree()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

    Canny(matInput, matResult, 50, 100);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_CartoonCameraActivity_ConvertRGBtoCartoonFour(JNIEnv *env, jobject thiz,
                                                                     jlong mat_addr_input,
                                                                     jlong mat_addr_result,
                                                                     jlong mat_addr_capture) {
    // TODO: implement ConvertRGBtoCartoonFour()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;
    Mat &matCapture = *(Mat *)mat_addr_capture;

    bitwise_not(matInput, matResult);
    // 2??? ??????????????? capture??? ????????? (matInput??? ?????? ????????? ???????????? bitwise??? ?????? ??????????????? ?????? -> ?????? ?????? ??????????????? matCapture??? ?????????)
    // matResult??? ?????? ?????? ?????????????????? ?????? ???????????? ????????? ????????? ???????????? ????????? ??? ?????????
    // ????????? matInput??? 2??? ??????????????? ????????? ??????
    bitwise_not(matInput, matCapture);
    bitwise_not(matCapture, matCapture);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_BlurCameraActivity_ConvertRGBtoBlur(JNIEnv *env, jobject thiz,
                                                                      jlong mat_addr_input,
                                                                      jlong mat_addr_result,
                                                                      jlong mat_addrt_capture) {
    // TODO: implement ConvertRGBtoBlur()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;
    Mat &matCapture = *(Mat *)mat_addrt_capture;

    GaussianBlur(matInput, matResult, Size(7,7), 0);
    GaussianBlur(matInput, matCapture, Size(7,7), 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_cvpro_filtercams_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                                jlong mat_addr_input,
                                                                jlong mat_addr_result,
                                                                jlong mat_addrt_capture) {
    // TODO: implement ConvertRGBtoGray()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;
    Mat &matCapture = *(Mat *)mat_addrt_capture;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
    cvtColor(matInput, matCapture, COLOR_RGBA2GRAY);
}