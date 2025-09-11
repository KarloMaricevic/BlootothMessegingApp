pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BlootothMessegingApp"
include(":app")
include(":domain")
include(":bluetooth")
include(":core:platform")
include(":core:common")
include(":core:navigation")
include(":data")
include(":feature")
include(":designSystem")
include(":core:dispatchers")
