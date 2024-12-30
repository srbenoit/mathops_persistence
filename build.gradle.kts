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
    implementation("org.postgresql:postgresql:42.7.3")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation(files("../mathops_commons/out/libs/mathops_commons.jar"))
    implementation(files("../mathops_text/out/libs/mathops_text.jar"))

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.javaModuleVersion = provider { "1.0" }
}