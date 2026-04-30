plugins {
    id("java")
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

    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:1.0.0")
    //implementation("com.redhat.devtools.intellij:lsp4ij:0.19.3")
}


tasks.test {
    useJUnitPlatform()
}