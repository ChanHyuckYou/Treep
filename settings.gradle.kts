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
        maven { url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/") }
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven {url = uri("https://naver.jfrog.io/artifactory/maven/")}
        maven { url = uri ("https://www.jitpack.io") }
        maven { url = uri("https://devrepo.kakaomobility.com/repository/kakao-mobility-android-knsdk-public/")}

        mavenCentral()

    }
}



rootProject.name = "My Application"

include (":app", )
