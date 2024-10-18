project.version = "0.0.0";

val PROJECT_NAME = project.extra["PROJECT_NAME"];

val PROJECT_ROOT_NAME = project.extra["PROJECT_ROOT_NAME"];

val PROJECT_MODULE_NAME = project.extra["PROJECT_MODULE_NAME"];

project.java {
  this.modularity.inferModulePath.set(true);
  this.sourceCompatibility = JavaVersion.VERSION_1_9;
  this.targetCompatibility = JavaVersion.VERSION_1_9;
}

project.application {
  this.mainModule.set(PROJECT_MODULE_NAME.toString());
  this.mainClass.set("${PROJECT_MODULE_NAME}.MainExe");
}

project.dependencies {
//  this.implementation(
//    ":vosk-0.3.45"
//  )
  this.implementation(
    "com.alphacephei",
    "vosk",
    "0.3.45"
  )
}