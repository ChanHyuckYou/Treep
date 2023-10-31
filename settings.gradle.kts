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
        maven { setUrl("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/") }
        mavenCentral()

    }
}


rootProject.name = "My Application"
include(":app")
