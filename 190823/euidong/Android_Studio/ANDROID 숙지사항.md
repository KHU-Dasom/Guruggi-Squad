# Android ���߿��� �˾ƾ��� ��.

language ������ java�� kotlin�� �����Ѵ�. (kotlin�� java�� �Ϲ������δ� ������Ų ����)

## *�⺻���� ���� ���*
### 1. app > java > com.example.���̸� > MainActivity  main�Լ���� �� �� �ִ�.
app�� logical�� ������ �ڵ��ϴ� �κ��̴�.

### 2. app > res > layout > activity_main.xml  layout�� ������.
layout�� � text button���� ���� � ��ġ�� ������ �ܰ����� �κ��� �����ϴ� �κ��̴�.

### 3. app > manifest > AndroidManifest.xml  �ȵ���̵��� �⺻������ ������ �� ����.
�ȵ���̵� ���� �������� �κ��� �����ϴ� �κ��̴�. ����, ���� ȹ���� ���ϴ� ��� ���⿡ ������ ���־�� �Ѵ�.

### 4. Gradle Scripts > build.gradle �������ϰ� build�ϱ� ���� ������ ��. 
�� �� ū ������ �ϴ� ���� ������Ʈ�̴� ���� ��� SDK ���� �����̶���� �� ���� ��ü���� ū ����� �����ϰ�, 
���� �׺��� ���� �ܰ�� IMPORT�� ���̺귯���� �����ϴ� ���� ������ �Ѵ�.

## *�����ֱ�*
���� -> onCreate ȣ�� -> onStart ȣ�� -> onResume ȣ��

onPause �ÿ��� onStop OR onCreate OR onResume���� �̵�����

onStop �ÿ��� onDestroy OR onCreate OR onRestart�� �̵�����

onDestroy �ÿ��� ����ȴ�.

onRestart �ÿ��� onStart�� ���ư���.

�ش��ϴ� �ܰ��� �Լ��� �������̵��Ͽ� ���� �����Ѵ�.


## AR Core

### 1. �� ���� ������ �����Ѵ�. ����� ���Ѵ�.
- AR Optional(AR Core�� �������� �ʴ� ��ġ������ ����� �� �ִ� ���.)

- AR Required(���� ���� �̻󿡼��� ��밡�� AR Core�� �����ϴ� ��ġ������ ������ �� �ִ� ����̰�, 

�ش� ��ġ�� AR Core�� ��ġ�Ǿ� ���� �ʴٸ� �ڵ����� ��ġ�ϰ� bundle ������ ���־�� �Ѵ�.)

�ش� ��ĺ��� ���� AndroidManifest.xml �� ������ �ٲ��־���Ѵ�.

=> AR Optional
```
<!-- "AR Optional" apps must declare minSdkVersion �� 14 -->
<uses-sdk android:minSdkVersion="14" />

<uses-permission android:name="android.permission.CAMERA" />

<application>
    ��
    <!-- Indicates that the app supports, but does not require ARCore ("AR Optional").
         Unlike "AR Required" apps, the Google Play Store will not automatically
         download and install Google Play Services for AR when the app is installed.
    -->
    <meta-data android:name="com.google.ar.core" android:value="optional" />
</application>
```

=> AR Required
```
<!-- "AR Required" apps must declare minSdkVersion �� 24 -->
<uses-sdk android:minSdkVersion="24" />

<uses-permission android:name="android.permission.CAMERA" />

<!-- Indicates that the app requires ARCore ("AR Required"). Ensures the app is
     visible only in the Google Play Store on devices that support ARCore.
-->
<uses-feature android:name="android.hardware.camera.ar" />

<application>
    ��
    <!-- Indicates that the app requires ARCore ("AR Required"). Causes the Google
         Play Store to download and install Google Play Services for AR when the
         app is installed.
    -->
    <meta-data android:name="com.google.ar.core" android:value="required" />
</application>
```
### 2. build.gradle ������ �ٲ��ش�.
=> project
```
allprojects {
    repositories {
        google()
        ��
```

=> app
```
dependencies {
    ��
    implementation 'com.google.ar:core:1.11.0'
}
```
### 3. runtime check�ϱ�.(optional�� ��츸)
Optional�� ��쿡�� ����Ѵ�. �ش� ��ġ�� AR Core�� ����ϴ��� ���ϴ����� üũ�ϴ� ����� �ִ�.

(ArCoreApk.checkAvailability()�� �̿��ϸ� �ش� ��Ⱑ ArCore�� ����ϴ��� �� �� �ִ�.)

### 4. ī�޶� ��� ������ ��û�϶�.
Sceneform�� �̿��ϸ�, ArFragment�� �ڵ������� ī�޶� �������� ��û�Ѵ�.(���� �� ������ �پ���� �� �ִ�.)

### 5. AR ���񽺰� �ش� ��⿡ ��ġ�Ǿ� �ִ����� Ȯ��.
Sceneform�� �̿��ϸ�, ArFragment�� �ڵ������� ī�޶� �������� ��û�Ѵ�.(���� �� ������ �پ���� �� �ִ�.)
