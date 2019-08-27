# Android 개발에서 알아야할 것.

language 설정은 java와 kotlin이 존재한다. (kotlin은 java를 일반적으로는 발전시킨 형태)

## *기본적인 파일 경로*
### 1. app > java > com.example.앱이름 > MainActivity  main함수라고 할 수 있다.
app의 logical한 동작을 코딩하는 부분이다.

### 2. app > res > layout > activity_main.xml  layout을 보여줌.
layout의 어떤 text button등을 쓸지 어떤 위치에 넣을지 외관적인 부분을 설정하는 부분이다.

### 3. app > manifest > AndroidManifest.xml  안드로이드의 기본설정을 정의할 수 있음.
안드로이드 앱의 보여지는 부분을 설정하는 부분이다. 또한, 권한 획득을 원하는 경우 여기에 선언을 해주어야 한다.

### 4. Gradle Scripts > build.gradle 컴파일하고 build하기 위한 설정을 함. 
좀 더 큰 설정을 하는 곳이 프로젝트이다 예를 들어 SDK 버전 설정이라든지 와 같은 전체적인 큰 기능을 설정하고, 
앱은 그보단 낮은 단계로 IMPORT할 라이브러리를 설정하는 등의 역할을 한다.

## *생명주기*
시작 -> onCreate 호출 -> onStart 호출 -> onResume 호출

onPause 시에는 onStop OR onCreate OR onResume으로 이동가능

onStop 시에는 onDestroy OR onCreate OR onRestart로 이동가능

onDestroy 시에는 종료된다.

onRestart 시에는 onStart로 돌아간다.

해당하는 단계의 함수를 오버라이딩하여 앱을 설계한다.


## AR Core

### 1. 두 가지 종류가 존재한다. 방식을 정한다.
- AR Optional(AR Core를 지원하지 않는 장치에서도 실행실 수 있는 방식.)

- AR Required(일정 버전 이상에서만 사용가능 AR Core를 지원하는 장치에서만 실행할 수 있는 방식이고, 

해당 장치에 AR Core가 설치되어 있지 않다면 자동으로 설치하게 bundle 설정을 해주어야 한다.)

해당 방식별로 설정 AndroidManifest.xml 의 설정을 바꿔주어야한다.

=> AR Optional
```
<!-- "AR Optional" apps must declare minSdkVersion ≥ 14 -->
<uses-sdk android:minSdkVersion="14" />

<uses-permission android:name="android.permission.CAMERA" />

<application>
    …
    <!-- Indicates that the app supports, but does not require ARCore ("AR Optional").
         Unlike "AR Required" apps, the Google Play Store will not automatically
         download and install Google Play Services for AR when the app is installed.
    -->
    <meta-data android:name="com.google.ar.core" android:value="optional" />
</application>
```

=> AR Required
```
<!-- "AR Required" apps must declare minSdkVersion ≥ 24 -->
<uses-sdk android:minSdkVersion="24" />

<uses-permission android:name="android.permission.CAMERA" />

<!-- Indicates that the app requires ARCore ("AR Required"). Ensures the app is
     visible only in the Google Play Store on devices that support ARCore.
-->
<uses-feature android:name="android.hardware.camera.ar" />

<application>
    …
    <!-- Indicates that the app requires ARCore ("AR Required"). Causes the Google
         Play Store to download and install Google Play Services for AR when the
         app is installed.
    -->
    <meta-data android:name="com.google.ar.core" android:value="required" />
</application>
```
### 2. build.gradle 설정을 바꿔준다.
=> project
```
allprojects {
    repositories {
        google()
        …
```

=> app
```
dependencies {
    …
    implementation 'com.google.ar:core:1.11.0'
}
```
### 3. runtime check하기.(optional인 경우만)
Optional인 경우에만 사용한다. 해당 장치가 AR Core를 사용하는지 안하는지를 체크하는 기능이 있다.

(ArCoreApk.checkAvailability()를 이용하면 해당 기기가 ArCore를 사용하는지 알 수 있다.)

### 4. 카메라 사용 권한을 요청하라.
Sceneform을 이용하면, ArFragment가 자동적으로 카메라 사용권한을 요청한다.(따라서 이 과정을 뛰어넘을 수 있다.)

### 5. AR 서비스가 해당 기기에 설치되어 있는지를 확인.
Sceneform을 이용하면, ArFragment가 자동적으로 카메라 사용권한을 요청한다.(따라서 이 과정을 뛰어넘을 수 있다.)
