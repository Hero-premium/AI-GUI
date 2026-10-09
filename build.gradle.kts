plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    kotlin("jvm")
}

val minJava = 26
if (JavaVersion.current() < JavaVersion.toVersion(minJava)) {
    throw GradleException("AI-GUI needs JDK $minJava or newer (found ${JavaVersion.current()}).")
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
    testImplementation(kotlin("test"))
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
kotlin {
    jvmToolchain(26)
}