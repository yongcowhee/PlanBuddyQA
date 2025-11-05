plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testng:testng:7.9.0")
    implementation("io.appium:java-client:10.0.0")
    testImplementation("net.sourceforge.tess4j:tess4j:5.16.0")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.38.0")
}

tasks.test {
    useJUnitPlatform()
}