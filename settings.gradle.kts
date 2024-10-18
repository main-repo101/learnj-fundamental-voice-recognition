settings.rootProject.name = "voice-recognition";

settings.include(":basic");
settings.include(":basic-no-jpms");

settings.gradle.beforeSettings {
    //REM: [TODO] .|. What is this? Why its look like not executing???
    print(String.format("::: settings.gradle.beforeSettings{ } %s\n", this.hashCode() ))
}

settings.gradle.settingsEvaluated {
    print(String.format("::: settings.gradle.settingsEvaluated{ } %s\n", this.hashCode() ))
}

settings.gradle.beforeProject {
    print(String.format("::: settings.gradle.beforeProject{ } %s\n", this.hashCode() ))
}

settings.gradle.projectsEvaluated {
    print(String.format("::: settings.gradle.projectsEvaluated{ } %s\n", this.hashCode() ))
}

settings.pluginManagement {
    this.repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    this.resolutionStrategy {

    }
}

settings.dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    this.repositories {
        this.mavenCentral();
        this.gradlePluginPortal();
        this.google();
        this.flatDir {
            this.dirs("lib/")
        }
    }
    this.versionCatalogs {
        this.register("unitTest") {
            this.from(files("version-catalog/unitTest.versions.toml"))
        }
    }
}
