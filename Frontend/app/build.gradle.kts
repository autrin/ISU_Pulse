plugins {
    alias(libs.plugins.android.application)
    // id("com.android.application") // Removed this line
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.coms309.isu_pulse_frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.coms309.isu_pulse_frontend"
        minSdk = 30
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17 // Updated to Java 17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-appcheck")
    implementation("com.google.firebase:firebase-messaging:24.0.3")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.navigation.ui)
    implementation(libs.volley)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.glide)
    implementation(libs.firebase.database)
    annotationProcessor(libs.glide.compiler)

    // Unit test dependencies
    testImplementation(libs.junit)
    testImplementation("org.robolectric:robolectric:4.9") // Robolectric for unit tests
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test:core:1.4.0")

    // Android instrumentation test dependencies
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.code.gson:gson:2.8.8")

    implementation("org.java-websocket:Java-WebSocket:1.5.2")

}

// Apply the Google Services plugin at the bottom of the file
apply(plugin = "com.google.gms.google-services")
