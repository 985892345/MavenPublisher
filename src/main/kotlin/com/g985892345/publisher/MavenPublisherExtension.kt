package com.g985892345.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
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
    val publisher = extensions.create("publisher", Publisher::class.java, project)
    publisher.publicationConfig.config(project)
    // 使用 afterEvaluate 确保 publish 已被设置
    afterEvaluate {
      extensions.configure<PublishingExtension> {
        publications {
          val projectGithubName = publisher.githubName
          val projectArtifactId = publisher.artifactId
          val projectGroupId = publisher.groupId
          val projectVersion = publisher.version ?: error("未设置 version, 请在 build.gradle.kts 中设置 version 或单独设置 publisher.version")
          val projectGithubRepositoryName = publisher.githubRepositoryName
          val projectDescription = publisher.description
          val projectMainBranch = publisher.mainBranch
          create<MavenPublication>("MavenCentral") {
            artifactId = projectArtifactId
            groupId = projectGroupId
            version = projectVersion
            publisher.publicationConfig.apply { configMaven(project) }
            extensions.configure<SigningExtension> {
              sign(this@create)
            }

            pom {
              name.set(projectArtifactId)
              description.set(projectDescription)
              url.set("https://github.com/$projectGithubName/$projectGithubRepositoryName")

              licenses {
                license {
                  name.set(publisher.license ?: error("未提供 License, 请在 build.gradle.kts 中设置 publisher.license = \"XXX\""))
                  url.set("https://github.com/$projectGithubName/$projectGithubRepositoryName/blob/$projectMainBranch/LICENSE")
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
                connection.set("https://github.com/$projectGithubName/$projectGithubRepositoryName.git")
                developerConnection.set("https://github.com/$projectGithubName/$projectGithubRepositoryName.git")
                url.set("https://github.com/$projectGithubName/$projectGithubRepositoryName")
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
      publisher.publicationConfig.afterConfigMaven(project)
    }
  }
}