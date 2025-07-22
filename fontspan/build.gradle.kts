import java.util.Properties
import java.io.FileInputStream

// Added: Reads the local.properties file from the root project
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "tech.rb.ora.fontspan"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    publishing {
        singleVariant("release")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Add all the missing test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.randysbondoc"
            artifactId = "fontspan"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/randysbondoc/Ora.AI")
            credentials {
                // Changed: Reads credentials securely from local.properties
                username = localProperties.getProperty("gpr.user")
                password = localProperties.getProperty("gpr.key")
            }
        }
    }
}