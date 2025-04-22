plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //Kotlin
    alias(libs.plugins.kotlinx.serialization)

    //Pasar del local.properties al AndroidManifest
    alias(libs.plugins.secrets.gradle.plugin)

    //Google
    id("com.google.gms.google-services")

    //Detekt
    id("io.gitlab.arturbosch.detekt")

}

android {
    namespace = "com.front_pes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.front_pes"
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
    implementation(libs.play.services.location)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Guardar Idioma
    implementation ("androidx.datastore:datastore-preferences:1.0.0")


    //Login API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    //GoogleMaps
    implementation(libs.google.maps.compose)
    implementation(libs.play.services.maps)
    implementation("com.google.maps.android:android-maps-utils:3.11.2")


    //Google Services
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //Compose
    implementation(libs.compose.navigation)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-auth-ktx")

    //Kotlin
    implementation(libs.kotlin.serialization.json)

    //Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")


}

detekt {
    toolVersion = "1.23.8"
    buildUponDefaultConfig = true
    input = files("src/main/java", "src/main/kotlin")
    config = files("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/baseline.xml")
}