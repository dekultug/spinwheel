import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.LogFactory.release
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
//    id ("com.google.firebase.crashlytics")
}

android {
    namespace = "com.amvfunny.dev.wheelist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amvfunny.dev.wheelist"
        minSdk = 24
        targetSdk = 34
        versionCode = 100
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val formattedDate = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault()).format(Date())
        base.archivesName = "App289_v$versionName($versionCode)_$formattedDate"
    }
    flavorDimensions ("default")

//    signingConfigs {
//        create("release") {
//            storeFile = file("D:\\tunglv\\keystorefile\\keystoreapp226.jks")
//            storePassword = "123456"
//            keyAlias = "key0"
//            keyPassword = "123456"
//        }
//    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("release")
        }

        debug {
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    bundle {
        language {
            enableSplit = false
        }
        density {
            // This property is set to true by default.
            enableSplit = true
        }
        abi {
            // This property is set to true by default.
            enableSplit = true
        }
    }

    productFlavors {
        create("develop") {
//            applicationIdSuffix = ".dev"
            buildConfigField("Long", "Minimum_Fetch", "5L")
        }

        create("production") {
            buildConfigField("Long", "Minimum_Fetch", "3600L")
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.app.update.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.glide)
    implementation(projects.library)
    implementation(projects.kavehColorPicker)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.eventbus)
    implementation (libs.lottie)
    implementation(libs.dotsindicator)
    implementation(libs.converter.gson)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)

    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-config:21.6.3")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("com.google.android.play:review-ktx:2.0.2")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC2")
    // Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")
}