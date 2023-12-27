package com.g985892345.publisher

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

/**
 * .
 *
 * @author 985892345
 * 2023/12/25 22:35
 */
interface MavenPublicationConfig {
  fun config(project: Project) {
    val publisher = project.extensions.create("publisher", Publisher::class.java, project)
    project.afterEvaluate {
      extensions.configure<PublishingExtension> {
        publications {
          create<MavenPublication>("MavenCentral") {
            artifactId = publisher.artifactId
            groupId = publisher.groupId
            version = publisher.version
            publisher.publicationConfig.apply { configMaven(project) }
            extensions.configure<SigningExtension> {
              sign(this@create)
            }

            pom {
              name.set(publisher.artifactId)
              description.set(publisher.description)
              url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}")

              licenses {
                license {
                  name.set(
                    publisher.license
                      ?: error("未提供 License, 请在 build.gradle.kts 中设置 publisher.license = \"XXX\"")
                  )
                  url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}/blob/${publisher.mainBranch}/LICENSE")
                }
              }

              developers {
                developer {
                  publisher.masterDeveloper.config(this)
                }
                publisher.otherDevelopers.forEach {
                  developer { it.config(this) }
                }
              }

              scm {
                connection.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}.git")
                developerConnection.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}.git")
                url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}")
              }
            }
          }
        }
      }
    }
  }
  fun MavenPublication.configMaven(project: Project)
  fun afterConfigMaven(project: Project) {
    project.tasks.register("toMavenCentral") {
      group = "maven publisher"
      dependsOn("publishMavenCentralPublicationToMavenCentralRepository")
    }

    project.tasks.register("toMavenLocal") {
      group = "maven publisher"
      dependsOn("publishMavenCentralPublicationToMavenLocal")
    }
  }
}

internal fun configJarTask(project: Project) {
  project.tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
    from("javadoc")
  }

  project.tasks.register("sourcesJar", Jar::class.java) {
    archiveClassifier.set("sources")
    val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
    from(sourceSets["main"].allSource)
  }
}
