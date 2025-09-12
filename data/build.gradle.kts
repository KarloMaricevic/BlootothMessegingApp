plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.karlomaricevic.bluetoothmessagingapp.data"
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
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.karlomaricevic.bluetoothmessagingapp.data.db")
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:dispatchers"))
    implementation(project(":core:platform"))
    implementation(project(":domain"))
    implementation(project(":bluetooth"))
    implementation(libs.androidx.core.ktx)

    implementation(libs.kodein.di)

    implementation(libs.sqldelight.runtime)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}