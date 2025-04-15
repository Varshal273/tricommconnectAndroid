plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.tricommconnect_v1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tricommconnect_v1"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.tracing.perfetto.handshake)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ✅ Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // ✅ Jetpack Compose UI
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    // ✅ Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ✅ Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ✅ Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ✅ Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // ✅ DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ✅ Accompanist (optional – for system UI, insets, etc.)
     implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.3-beta")

    // ✅ Material Icons (optional)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")


    // Coroutine support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // Compose LiveData / Flow support
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0")
    implementation ("androidx.compose.runtime:runtime:1.4.0")
}