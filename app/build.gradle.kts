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

    // KtLint
    id("org.jlleitschuh.gradle.ktlint")

    //Jacoco
    id("jacoco")

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
        manifestPlaceholders["MAPS_API_KEY"] = System.getenv("MAPS_API_KEY") ?: "MISSING_KEY"
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
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

    //Resources
    implementation("androidx.appcompat:appcompat-resources:1.7.0")

    //S3
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.12.0")

    //Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")

    implementation(libs.androidx.media3.common.ktx)

}

detekt {
    toolVersion = "1.23.8"
    buildUponDefaultConfig = true
    input = files("src/main/java", "src/main/kotlin")
    config = files("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

ktlint {
    version.set("0.48.2")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val debugTree = fileTree("${buildDir}/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }

    val kotlinDebugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(files("${buildDir}/jacoco/testDebugUnit.exec"))
}
