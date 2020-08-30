import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html

// To upload to sonatype (have to deploy manually)
// ./gradlew clean assemble dokkaJar publish

// I think if you can see it here, then it's ready to be "Closed" and deployed manually:
// https://oss.sonatype.org/content/groups/staging/org/organicdesign/Indented/
// Here once released:
// https://repo1.maven.org/maven2/org/organicdesign/Indented/

// This takes these values from ~/gradle.properties which should have valid values for each of these names in it.
// https://docs.gradle.org/current/userguide/build_environment.html
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.4.0-rc"
    id("com.github.ben-manes.versions") version "0.29.0"
    kotlin("jvm") version "1.4.0"
}
dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
}

group = "org.organicdesign"
version = "0.0.14"
description = "Make debugging methods whose String output compiles to valid Java or Kotlin and is pretty-print indented for easy reading."

java {
//    withJavadocJar()
    withSourcesJar()
}

// This doesn't work.  It's here for a Dokka question or bug report.
tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach { // Or source set name, for single-platform the default source sets are `main` and `test`
            // List of files with module and package documentation
            // https://kotlinlang.org/docs/reference/kotlin-doc.html#module-and-package-documentation
            includes = listOf("package.md", "module.md")
        }
    }
}

tasks.register<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    dependsOn("dokkaJavadoc")
    from("$buildDir/dokka/javadoc/")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            artifact(tasks["dokkaJar"])
            pom {
                name.set(rootProject.name)
                packaging = "jar"
                description.set(project.description)
                url.set("https://github.com/GlenKPeterson/Indented")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("GlenKPeterson")
                        name.set("Glen K. Peterson")
                        email.set("glen@organicdesign.org")
                        organization.set("PlanBase Inc.")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/GlenKPeterson/Indented.git")
                    developerConnection.set("scm:git:https://github.com/GlenKPeterson/Indented.git")
                    url.set("https://github.com/GlenKPeterson/Indented.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

repositories {
    jcenter()
    mavenCentral()
    maven(url="https://jitpack.io")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}