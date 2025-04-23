
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    //Google
    id("com.android.library") version "8.2.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

    //Detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false

    // KtLint
    id("org.jlleitschuh.gradle.ktlint") version "11.4.2" apply false
}

//test2