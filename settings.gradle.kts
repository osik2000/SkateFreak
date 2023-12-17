pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
//        maven {
//            name = "DailymotionMavenRelease"
//            url = uri("https://mvn.dailymotion.com/repository/releases/")
//        }
    }
}

rootProject.name = "SkateFreak"
include(":app")
 