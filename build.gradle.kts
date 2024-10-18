

val DEP_JUNIT_JUPITER_API = unitTest.junitJupiterAPI.get();
val DEP_JUNIT_JUPITER_ENGINE = unitTest.junitJupiterEngine.get();

project.allprojects {
}

project.subprojects {
    this.project.plugins.apply("java");
    this.project.plugins.apply("maven-publish");
    this.project.plugins.apply("ivy-publish");

    this.project.group = "learnj";

    val PROJECT_NAME = project.name
        .replace(Regex("[ .-]+"), "_")
        .replace(Regex("^_+|_+$"), "")

    val PROJECT_ROOT_NAME = project.rootProject.name
        .replace(Regex("[ .-]+"), "_")
        .replace(Regex("^_+|_+$"), "")

    val PROJECT_MODULE_NAME = String.format(
        "%s.%s.%s",
        project.group,
        PROJECT_ROOT_NAME,
        PROJECT_NAME
    );

    this.project.extra["PROJECT_NAME"] = PROJECT_NAME
    this.project.extra["PROJECT_ROOT_NAME"] = PROJECT_ROOT_NAME
    this.project.extra["PROJECT_MODULE_NAME"] = PROJECT_MODULE_NAME;

    if( this.project.name.lowercase().contains("lib") ) {
        this.project.plugins.apply("java-library");
    }
    else {
        this.project.plugins.apply("application");
    }

    this.project.plugins.withType<JavaPlugin> {
    }
    the<JavaPluginExtension>().apply {
        this.modularity.inferModulePath.set(true);
        this.sourceCompatibility = JavaVersion.VERSION_1_9;
        this.targetCompatibility = JavaVersion.VERSION_1_9;
    }

    this.project.dependencies {
        this.add(
            "testImplementation",
            DEP_JUNIT_JUPITER_API
        )
        this.add(
            "testRuntimeOnly",
            DEP_JUNIT_JUPITER_ENGINE
        )
    }

    this.project.tasks.withType<Test> {
        this.useJUnitPlatform();
    }

    this.project.tasks.withType<Jar> {
        this.manifest {
            this.attributes["Implementation-Vendor"] = project.group;
            this.attributes["Specification-Vendor"] = project.group;
        }
    }

}