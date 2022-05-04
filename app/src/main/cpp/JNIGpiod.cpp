// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("gpiod");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("gpiod")
//      }
//    }

#define LOG_TAG    "gpiod-lib_JNI"
#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <string>
#include <gpiod.h>
#include <android/log.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_gpiod_MainActivity_getGpioTotalBank(JNIEnv *env, jobject thiz) {
    std::ignore = thiz;
    struct gpiod_chip_iter *iter;
    struct gpiod_chip *chip;
    unsigned int total_bank = 0;

    iter = gpiod_chip_iter_new();
    gpiod_foreach_chip(iter, chip) {
        total_bank += 1;
    }

    gpiod_chip_iter_free(iter);

    return total_bank;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_gpiod_MainActivity_getGpioInfo(JNIEnv *env, jobject thiz, jint gpiobank, jint gpioline) {
    std::ignore = thiz;

    char bank[32] = {0}, ret[4] = {0};
    sprintf(bank, "gpiochip%d", gpiobank);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "GPIO gpiobank is %d", gpiobank);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "GPIO gpioline is %d", gpioline);

    jint value = gpiod_ctxless_get_value((char *)bank, gpioline, 0, "gpioget");

    sprintf(ret, "%d", value);

    return env->NewStringUTF(ret);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_gpiod_MainActivity_setGpioInfo(JNIEnv *env, jobject thiz, jint gpiobank, jint gpioline, jint gpio_value) {

    std::ignore = thiz;

    char bank[32] = {0}, ret_str[32] = {0};
    sprintf(bank, "/dev/gpiochip%d", gpiobank);

    int ret = gpiod_ctxless_set_value(bank, gpioline, gpio_value, 0, "gpioset", NULL, NULL);

    if(ret)
        sprintf(ret_str, "Set Failed !");
    else
        sprintf(ret_str, "Set Successed !");

    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "set gpio ret = %d", ret);
    return env->NewStringUTF(ret_str);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_gpiod_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    // TODO: implement stringFromJNI()
    return env->NewStringUTF("Test");
}