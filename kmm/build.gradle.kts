plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("kotlinx-serialization")
}

version = "1.0.0"

kotlin {
	apply(from = "./versions.gradle.kts")
	val coroutineVersion: String by extra
	val kodeinVersion: String by extra
	val serializationVersion: String by extra
	val kotlinxDatetimeVersion: String by extra
	val ktorVersion: String by extra

	android()

	sourceSets.all {
		languageSettings.optIn("kotlin.RequiresOptIn")
	}

	sourceSets {
		dependencies {
			coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")
		}

		val commonMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
				implementation("org.kodein.di:kodein-di:$kodeinVersion")
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
				implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
				implementation("io.ktor:ktor-client-core:$ktorVersion")
				implementation("io.ktor:ktor-client-serialization:$ktorVersion")
				implementation("io.ktor:ktor-client-logging:$ktorVersion")
				implementation("io.ktor:ktor-client-auth:$ktorVersion")
				implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
				implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val androidMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlin:kotlin-stdlib")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
				implementation("io.ktor:ktor-client-android:$ktorVersion")
				implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
				implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
				implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

			}
		}
		val androidTest by getting
	}
}

android {
	compileSdk = 33
	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
	defaultConfig {
		minSdk = 21
		targetSdk = 33
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	namespace = "fr.beapp.interviews.bicloo.kmm"

	buildTypes {
		getByName("debug") {
			isMinifyEnabled = false
		}
		getByName("release") {
			isMinifyEnabled = true
		}
	}
}

// compile bytecode to java 8
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}
