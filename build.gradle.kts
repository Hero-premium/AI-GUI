plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.code.gson:gson:2.14.0")
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls")  // add "javafx.fxml" only if you end up using FXML
}

application {
    mainClass.set("org.hero.Main")
}

tasks.test {
    useJUnitPlatform()
}