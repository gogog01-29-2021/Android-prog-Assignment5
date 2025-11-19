plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Plugin for Kotlin Parcelize to enable @Parcelize annotation
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.assignment5"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.assignment5"
        minSdk = 33
        targetSdk = 36
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
        // Enable Jetpack Compose for modern UI components
        compose = true
        // Enable ViewBinding to replace findViewById() calls with type-safe view references
        viewBinding = true
    }
}

dependencies {
    // Core Android KTX library provides Kotlin extensions
    implementation(libs.androidx.core.ktx)
    // AppCompat library for backward compatibility
    implementation("androidx.appcompat:appcompat:1.6.1")
    // Material Design components library
    implementation("com.google.android.material:material:1.11.0")
    // Fragment KTX library for fragment management with Kotlin extensions
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    // Lifecycle runtime for managing app lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose dependencies (keeping for potential future use)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}