package com.g985892345.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

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
        repositories {
          maven {
            // https://s01.oss.sonatype.org/
            name = "mavenCentral"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val isSnapshot = publisher.version.endsWith("SNAPSHOT")
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