plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.map.secret)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.qrscanner"
    compileSdk = 34


    buildFeatures {
        viewBinding = true
    }



    defaultConfig {
        applicationId = "com.example.qrscanner"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.viewbinding)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.google.maps)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.barcode.scanning)
    implementation("androidx.camera:camera-core:1.2.0")
    implementation("androidx.camera:camera-camera2:1.2.1")
    implementation("androidx.camera:camera-lifecycle:1.2.1")
    implementation("androidx.camera:camera-view:1.2.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
}