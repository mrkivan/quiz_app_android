plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    // Hilt Plugin
    alias(libs.plugins.hilt.android)

    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tnm.quizmaster"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tnm.quizmaster"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://mrkivan.github.io/quiz_app_data/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://production.yourdomain.com/api/\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }

    hilt {
        enableAggregatingTask = false
    }
}
kotlin {
    jvmToolchain(11) // instead of jvmTarget = "11"

    compilerOptions {
        // Add any free compiler args here
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
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
    implementation(libs.androidx.navigationevent)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    // Hilt for AndroidX (e.g., Jetpack Compose ViewModels)
    implementation(libs.androidx.hilt.navigation.compose)

    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.lottie.compose)
    // Core testing
        testImplementation(libs.junit)
        testImplementation(libs.androidx.core.testing) // For InstantTaskExecutorRule
        // Coroutines testing
        testImplementation(libs.kotlinx.coroutines.test) // Use the latest version
        testImplementation(libs.kotlin.test) // for kotlin.test.assertEquals
        // --- MockK (for mocking coroutines, suspend functions, etc.) ---
        testImplementation(libs.mockk)
        // --- Turbine (Flow testing) ---
        testImplementation(libs.turbine)
}