// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // This file uses a different syntax. We use alias() here.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}