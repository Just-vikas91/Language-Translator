plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.langtrans"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.langtrans"
        minSdk = 24
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

    implementation("com.google.gms:google-services:4.4.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    // dependency for firebase core.
    implementation("com.google.firebase:firebase-core:21.1.1")
    // below two dependency are used for language detection
    implementation("com.google.firebase:firebase-ml-natural-language:22.0.1")
    implementation("com.google.firebase:firebase-ml-natural-language-translate-model:20.0.9")
//    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
//    implementation("com.google.android.gms:play-services-tasks:18.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}