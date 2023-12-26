package com.g985892345.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.plugins.signing.SigningExtension

/**
 * 一键发布到 mavenControl 仓库，自动判断 Android 、Kotlin、Java 平台
 *
 * @author 985892345
 * 2023/12/25 21:27
 */
class MavenPublisherExtension : Plugin<Project> {

  override fun apply(target: Project) {
    target.config()
  }

  private fun Project.config() {
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.gradle.signing")
    val publish = extensions.create("publisher", Publisher::class.java, project)
    publish.publicationConfig.config(project)
    // 使用 afterEvaluate 确保 publish 已被设置
    afterEvaluate {
      extensions.configure<PublishingExtension> {
        publications {
          val projectArtifactId = publish.artifactId
          val projectGroupId = publish.groupId ?: error("未设置 groupId")
          val projectVersion = publish.version ?: error("未设置 version")
          val projectGithubName = publish.githubName
          val projectDescription = publish.description
          val projectMainBranch = publish.mainBranch
          create<MavenPublication>("MavenCentral") {
            artifactId = projectArtifactId
            groupId = projectGroupId
            version = projectVersion
            publish.publicationConfig.apply { configMaven(project) }
            extensions.configure<SigningExtension> {
              sign(this@create)
            }

            pom {
              name.set(projectArtifactId)
              description.set(projectDescription)
              url.set("https://github.com/985892345/$projectGithubName")

              licenses {
                license {
                  name.set(publish.license ?: error("未提供 License"))
                  url.set("https://github.com/985892345/$projectGithubName/blob/$projectMainBranch/LICENSE")
                }
              }

              developers {
                developer {
                  id.set("985892345")
                  name.set("GuoXiangrui")
                  email.set("guo985892345@formail.com")
                }
              }

              scm {
                connection.set("https://github.com/985892345/$projectGithubName.git")
                developerConnection.set("https://github.com/985892345/$projectGithubName.git")
                url.set("https://github.com/985892345/$projectGithubName")
              }
            }
          }
          repositories {
            maven {
              // https://s01.oss.sonatype.org/
              name = "mavenCentral"
              val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
              val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
              val isSnapshot = projectVersion.endsWith("SNAPSHOT")
              setUrl(if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl)
              credentials {
                username = project.properties["mavenCentralUsername"].toString()
                password = project.properties["mavenCentralPassword"].toString()
              }
            }
          }
        }
      }

      tasks.create("publishToMavenCentral") {
        group = "publishing"
        dependsOn(tasks.getByName("publishAllPublicationsToMavenCentralRepository"))
      }
    }
  }
}