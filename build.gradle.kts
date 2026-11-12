plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "9.6.1"
    kotlin("jvm")
    jacoco
}

val minJava = 25
if (JavaVersion.current() < JavaVersion.toVersion(minJava)) {
    throw GradleException("AI-GUI needs JDK $minJava or newer (found ${JavaVersion.current()}).")
}

group = "io.github.hero_premium"
version = "v0.1.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.15"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("ch.qos.logback:logback-classic:1.6.3")
    implementation("org.xerial:sqlite-jdbc:3.53.2.1")
    implementation("com.ezylang:EvalEx:3.7.0")
    compileOnly("org.jetbrains:annotations:26.1.0")
}

javafx {
    version = "26.0.2"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("io.github.hero_premium.program.Main")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

kotlin {
    jvmToolchain(minJava)
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required = true
        xml.required = true
    }
}

    tasks.shadowJar {
        archiveClassifier.set("")

        manifest {
            attributes["Main-Class"] = "io.github.hero_premium.program.Main"
        }

        mergeServiceFiles()

        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA"
        )
    }