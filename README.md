# kotlin_gpiod_demo

A gpio control demo using modular libgpiod way which is packaged a JNI library, users can calling JNI library using JAVA/Kotlin apis, this demo adapt Kotlin and Jetpack Compose to write front-end part.


## Supported Hardware
 
|SoC|Device Under Test|Target OS|Status|
|---|---|---|---|
|NXP i.MX8M-Plus | EDM-G-IMX8MP| Android 12 <br> Android 11 |&#10004;|
|NXP i.MX8M-Mini | PICO-IMX8MM| Android 11|&#10004;|
|Broadcom BCM2711 |Raspberry PI 4B| Android 12 | &#10004;|
|AllWinner H3|Orange PI PC| Android 12 | &#10004;|

## Compile APK steps

1. Download latest Android-Studio
Visit [Google Android Developer webpage](https://developer.android.com/studio) and download it according your host OS such as Windows or Linux.
2. Open Android-Studio after installed, click File -> Open and choose the pahe of kotlin_gpiod_demo folder.
3. Waiting upgrade and build Gradle relate packages.
4. Click Build -> Select Build Variant, choose debug|release mode and armeabi-v7a(ARM32)|arm64-v8a(ARM64) ABI version.
5. Click Build -> Build Bundle(s)/APK(s) to compile apk
6. Connect target device using USB OTG cable, wait Android-Studio detect your device, and click arrow icon (Run app) to deploy apk and run. e.g. EDM-G-IMX8MP:

![deploy-apk](images/deploy_apk.png)


## Develop your own app using gpiod JNI

* JNI library structure
  * API: open source
  * Native library: static library using libgpiod v1.4
  * Supported platforms: armeabi-v7a, arm64-v8a

#### Import steps

1. Add 'cpp' folder inside the app folder on your Android-Studio project.
2. Copy all files from 'cpp' folder of kotlin_gpiod_demo to your cpp folder which just created.
3. Add cpp relate part including cmake to build.gradle(:app), for example:
```
  defaultConfig {
        applicationId "com.example.gpiod"
        minSdk 28
        targetSdk 31
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }

        externalNativeBuild {
            cmake {
                cppFlags ''
                abiFilters 'armeabi-v7a', 'arm64-v8a'
            }
            ndk {
                // Specifies the ABI configurations of your native
                // libraries Gradle should build and package with your app.
                abiFilters 'armeabi-v7a', 'arm64-v8a'
            }
        }
    }

    Skip....
    
    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
    externalNativeBuild {
        cmake {
            path file('src/main/cpp/CMakeLists.txt')
            version '3.18.1'
        }
    }
```

4. Try to use loadLibrary api to call JNI API in your MainActivity.java or MainActivity.kt
```
    external fun stringFromJNI(): String
    external fun getGpioTotalBank(): Int
    external fun setGpioInfo(bank: Int, line: Int, value: Int): String
    external fun getGpioInfo(bank: Int, line: Int): String

    companion object {
        // Used to load the 'myapplication' library on application startup.
        init {
            System.loadLibrary("JNIGpiod")
        }
    }
```

## SELinux and Permission
Note that if you just deploy apk to target device directly, this apk will be an untrusted_app permission in Android SELinux policy. Even if you got target device key, and sign to this apk, it still as a platform_app permission in Android runtime SELinux policy, although better than untrusted_app but still doesn't works.

* Quick Solution
  * Issue command to Diable SELinux (Permissive mode):
  ```
  # setenforce 0
  ```

* Best Solution
  * Create fully support policy on Android BSP source code, it's not major topic here, but welcome to discuss with me how to fix SELinux policy issue if you need, my mail is onlywig@gmail.com

And don't forget give read/write permission for gpiochip device nodes if Android OS has no avaliable permission:
```
# chmod 666 /dev/gpiochip*
```
