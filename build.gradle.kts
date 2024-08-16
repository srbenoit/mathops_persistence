plugins {
    `java-library`
}

sourceSets {
    main {
        output.setResourcesDir(file("build/classes/java/main"))
    }
}

group = "dev.mathops.persistence"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ibm.informix:jdbc:4.50.11")
    implementation("com.oracle.database.jdbc:ojdbc11:23.4.0.24.05")
    implementation("io.undertow:undertow-core:2.3.15.Final")
    implementation("io.undertow:undertow-servlet:2.3.15.Final")
    implementation("io.undertow:undertow-websockets-jsr:2.3.15.Final")

    implementation(files("lib/mathops_commons.jar"))

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.javaModuleVersion = provider { "1.0" }
}