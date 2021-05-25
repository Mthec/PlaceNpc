plugins {
    java
}

group = "mod.wurmunlimited.npcs"
version = "0.2"
val wurmServerFolder = "E:/Steam/steamapps/common/Wurm Unlimited/WurmServerLauncher/"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(project(":WurmTestingHelper"))
    implementation(fileTree(wurmServerFolder) { include("server.jar") })
    implementation(fileTree(wurmServerFolder) { include("modlauncher.jar", "javassist.jar") })
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}