plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.hero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("ch.qos.logback:logback-classic:1.6.3")
}

javafx {
    version = "26.0.2"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("org.hero.Main")
}

tasks.test {
    useJUnitPlatform()
}