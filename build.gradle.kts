import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

// Deploying to OSSRH with Gradle
// https://central.sonatype.org/pages/gradle.html

// To upload to sonatype (have to deploy manually)
// ./gradlew clean uploadArchives --info

// I think if you can see it here, then it's ready to be "Closed" and deployed manually:
// https://oss.sonatype.org/content/groups/staging/org/organicdesign/indented/Indented/
// Or maybe here once released:
// https://repo.maven.apache.org/maven2/org/organicdesign/indented/Indented/

val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.ben-manes.versions") version "0.28.0"
    kotlin("jvm") version "1.3.71"
}

//repositories {
//    mavenLocal()
////    jcenter()
//    maven {
//        url.set("http://repo.maven.apache.org/maven2")
//    }
//}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
}

//task sourcesJar(type: Jar) {
//    classifier = "sources"
//    from(sourceSets.main.allSource)
//}
//
//task javadocJar(type: Jar) {
//    classifier = "javadoc"
//    from(javadoc.destinationDir)
//}

group = "org.organicdesign.indented"
//archivesBaseName = "Indented"
version = "0.0.11"
description = "Make debugging methods whose String output compiles to valid Java or Kotlin and is pretty-print indented for easy reading."
//sourceCompatibility = "1.8"

//artifacts {
//    archives javadocJar, sourcesJar
//}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
//                name.set("Indented")
//                description.set("Make debugging methods whose String output compiles to valid Java or Kotlin and is pretty-print indented for easy reading.")
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
//        mavenDeployer {
//            beforeDeployment { MavenDeployment deployment -> signing.signPom(deployment) }
//
//            repository(url: "") {
//                authentication(userName: ossrhUsername, password: ossrhPassword)
//            }
//
//            snapshotRepository(url: "") {
//                authentication(userName: ossrhUsername, password: ossrhPassword)
//            }
//        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}
//dokka {
//    outputFormat = "html"
//    outputDirectory = "$buildDir/javadoc"
//}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}


tasks.compileJava {
    options.encoding = "UTF-8"
}
repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/dokka") }
    maven { url = uri("https://jitpack.io") }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}