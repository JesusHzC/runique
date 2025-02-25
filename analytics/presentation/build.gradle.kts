plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.jesushz.analytics.presentation"

}

dependencies {
    implementation(projects.analytics.domain)
}