plugins {
    kotlin("jvm") version "2.0.0"
}

group = "org.db.migration"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ibm.db2:jcc:11.5.6.0")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}