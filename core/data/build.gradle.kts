plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
}

android {
    namespace = "com.jesushzc.core.data"
}

dependencies {

    implementation(libs.timber)
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.core.util)
}