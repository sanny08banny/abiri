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
        maven {
            credentials {
                username = "sanny08babby"
                password = "200@Pilot"
            }
            url = uri("https://repositories.tomtom.com/artifactory/maven")
        }
    }
}

rootProject.name = "Abiri"
include(":app")
 