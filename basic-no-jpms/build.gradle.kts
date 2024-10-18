

project.version = "0.0.0"

val PROJECT_NAME = project.extra["PROJECT_NAME"];

val PROJECT_ROOT_NAME = project.extra["PROJECT_ROOT_NAME"];

val PROJECT_MODULE_NAME = project.extra["PROJECT_MODULE_NAME"];

project.application {
  this.mainClass.set(
    "${PROJECT_MODULE_NAME}.MainExe"
  )
}

project.dependencies {
//  this.implementation(":vosk-0.3.45");
  this.implementation(
    "com.alphacephei",
    "vosk",
    "0.3.45"
  );
}