import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Properties

plugins {
	id("com.android.application")
	kotlin("android")

}

val (versionMajor, versionMinor, versionPatch, versionBuild) = listOf(1, 0, 0, 0)

fun generateVersionCode() =
	versionMajor * 1_000_000 + versionMinor * 10_000 + versionPatch * 100 + versionBuild

fun generateVersionName(): String {
	val isRelease = gradle.startParameter.taskRequests.toString().lowercase().contains("release")
	val isProd = gradle.startParameter.taskRequests.toString().lowercase().contains("prod")

	// display build number except for prod release
	return if (isRelease || isProd) {
		"$versionMajor.$versionMinor.$versionPatch"
	} else {
		"$versionMajor.$versionMinor.$versionPatch-$versionBuild"
	}
}

android {
	apply(from = "../versions.gradle.kts")

	compileSdk = 33

	namespace = "fr.beapp.interviews.bicloo.andro"

	defaultConfig {
		minSdk = 21
		targetSdk = 33
		versionCode = generateVersionCode()
		versionName = generateVersionName()
		setProperty("archivesBaseName", versionName)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
	}

	signingConfigs {

	}

	flavorDimensions.add("env")

	val properties = Properties()
	val localProperties = File("local.properties")
	if (localProperties.exists() && localProperties.isFile) {
		InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
			properties.load(reader)
		}
	} else {
		throw FileNotFoundException("local.properties file not found")
	}

	val jCDecauxApiKey = properties.getProperty("JCDECAUX_API_KEY")

	productFlavors {
		// clone flavors
		create("qa") {
			dimension = "env"
			buildConfigField("String", "JCDECAUX_API", "\"api.jcdecaux.com/vls/v1\"")
			buildConfigField("String", "JCDECAUX_API_KEY", "\"$jCDecauxApiKey\"")
		}

		create("preprod") {
			dimension = "env"
			buildConfigField("String", "JCDECAUX_API", "\"api.jcdecaux.com/vls/v1\"")
			buildConfigField("String", "JCDECAUX_API_KEY", "\"$jCDecauxApiKey\"")
		}

		create("prod") {
			dimension = "env"
			buildConfigField("String", "JCDECAUX_API", "\"api.jcdecaux.com/vls/v1\"")
			buildConfigField("String", "JCDECAUX_API_KEY", "\"$jCDecauxApiKey\"")
		}
	}

	buildTypes {
		getByName("debug") {
			applicationIdSuffix = ".debug"
			versionNameSuffix = "-debug"
			isMinifyEnabled = false
			isDebuggable = true
			signingConfig = signingConfigs.getByName("debug")
		}

		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
	}

	compileOptions {
		isCoreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		viewBinding = true
	}
}

dependencies {


	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")
	// basics
	implementation("androidx.core:core-ktx:1.10.1")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.9.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")

	//kmm
	implementation(project(":kmm"))

	// Image
	implementation("io.coil-kt:coil:2.3.0")
	implementation("io.coil-kt:coil-svg:2.2.2")

	// lifecycle
	val lifecycleVersion = "2.5.1"
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
	implementation("androidx.activity:activity-ktx:1.7.2")

	// google services
	implementation("com.google.android.gms:play-services-auth:20.5.0")

	// firebase
	implementation(platform("com.google.firebase:firebase-bom:31.1.1"))

}