plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.karlomaricevic.bluetoothmessagingapp.feature"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:platform"))
    implementation(project(":core:dispatchers"))
    implementation(project(":domain"))
    implementation(project(":designSystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.activity)

    implementation(libs.appcompanist.permissions)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.kodein.di)

    testImplementation(libs.junit)
    testImplementation(libs.kotest)
    testImplementation(libs.mockK)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.kotest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockK.android)
    debugImplementation(libs.ui.test.manifest)
}