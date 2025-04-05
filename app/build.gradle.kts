plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Google Services Plugin for Firebase
}

android {
    namespace = "com.nishant.disasteralertapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nishant.disasteralertapp"
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
}

dependencies {

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.material:material:1.10.0")


    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    implementation("com.google.android.libraries.places:places:3.3.0")

    implementation("com.android.volley:volley:1.2.1")



    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")






    // For getting user location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // For background tasks
    implementation("androidx.work:work-runtime:2.8.1")

    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")// Firebase BOM to ensure version compatibility
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))



    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")// Firebase BOM to ensure version compatibility
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))



    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")





    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1") // Keep only the latest version
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    // AndroidX dependencies (ensure these exist in `libs.versions.toml`, otherwise use explicit versions)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
