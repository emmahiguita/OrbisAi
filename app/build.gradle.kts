@file:Suppress("DEPRECATION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.orbisai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.orbisai"
        minSdk = 26
        targetSdk = 36
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
    
    // Configuración para resolver conflictos de recursos Java
    androidResources {
        noCompress += listOf("")
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/INDEX.LIST",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/*.kotlin_module",
                "META-INF/*.version",
                "META-INF/versions/9/previous-compilation-data.bin",
                "META-INF/room_master_table",
                "META-INF/room_schema_version",
                "/META-INF/{AL2.0,LGPL2.1}"
            )
            pickFirsts += listOf(
                "META-INF/gradle/incremental.annotation.processors",
                "META-INF/services/javax.annotation.processing.Processor",
                "META-INF/services/kotlinx.coroutines.CoroutineExceptionHandler",
                "META-INF/services/kotlinx.coroutines.internal.MainDispatcherFactory",
                "META-INF/services/com.google.auto.service.AutoService"
            )
            merges += listOf(
                "META-INF/services/**"
            )
        }
    }
}

// Configuración del Compose Compiler Plugin
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    // La siguiente línea se comenta para evitar el error de compilación.
    // stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
    includeSourceInformation = true
}

// Configuración de KSP para Room y Hilt
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
}

// Resolución de conflictos de versiones de Kotlin
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
        force("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
        force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.0")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
        
        // Resolver conflictos de META-INF
        preferProjectModules()
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Gemini (SDK oficial estable)
    implementation("com.google.genai:google-genai:1.15.0")

    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    // Navegación
    implementation(libs.navigation.compose)

    // ViewModel para Compose
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Animaciones
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-core")

    // Foundation
    implementation(libs.androidx.foundation)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.emoji2)
    ksp(libs.room.compiler)

    // Hilt con KSP (sin KAPT)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // KOOG AI Agents (JetBrains) - Temporalmente deshabilitado
    // implementation("ai.koog:koog-agents:0.4.1")

    // MCP Kotlin SDK
    implementation("io.modelcontextprotocol:kotlin-sdk-jvm:0.6.0")

    // DataStore (para configuraciones)
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Self SDK
    implementation("com.joinself:sdk-jvm:1.0.2")

    // TODO: Revisar dependencia de Hyperledger - aún no estable en repositorios públicos
    // implementation("org.hyperledger.identus:sdk-jvm:4.0.0-rc.7")
}