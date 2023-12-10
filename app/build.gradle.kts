plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "pl.pawelosinski.skatefreak"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.pawelosinski.skatefreak"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

// Import the Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
//
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // When using the BoM, don't specify versions in compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Coil - e.g. Photos from URL
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")

//    // DailyMotion Player
//    implementation ("com.dailymotion.player.android:sdk:1.0.8")
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//    implementation ("com.dailymotion.player.android:ads:1.0.8")
//    implementation ("com.google.ads.interactivemedia.v3:interactivemedia:3.31.0")
//

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}