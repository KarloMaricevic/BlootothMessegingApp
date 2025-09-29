plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvmToolchain(17)
    androidLibrary {
        namespace = "com.karlomaricevic.bluetoothmessagingapp.feature2"
        compileSdk = 36
        minSdk = 24
        androidResources.enable = true
        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    val xcfName = "feature2"

/*    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = xcfName
        }
    }*/
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":core:navigation"))
                implementation(project(":designSystem"))

                implementation(libs.kotlin.stdlib)

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)

                // Resources
                implementation(compose.components.resources)

                // Di
                implementation(libs.kodein.di)

                // Viewmodel
                implementation(libs.androidx.lifecycle.viewmodel.compose)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // ViewModel
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

                // Compose preview and tooling
                implementation(compose.uiTooling)
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
                implementation("androidx.activity:activity-compose:1.7.2")

                // Pooling container for Compose
                implementation("androidx.customview:customview-poolingcontainer:1.0.0")

                // Core KTX for ViewTree classes
                implementation("androidx.core:core-ktx:1.12.0")
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

/*        iosMain {
            dependencies {
            }
        }*/
    }

}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.karlomaricevic.bluetoothmessagingapp.feature2"
    generateResClass = always
}