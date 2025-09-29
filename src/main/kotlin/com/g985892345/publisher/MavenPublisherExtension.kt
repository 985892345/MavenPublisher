package com.g985892345.publisher

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * 基于 com.vanniktech.maven.publish 插件定制 github 一键发布到 mavenControl 仓库
 *
 * @author 985892345
 * 2023/12/25 21:27
 */
class MavenPublisherExtension : Plugin<Project> {

  override fun apply(target: Project) {
    target.config()
  }

  private fun Project.config() {
    apply(plugin = "com.vanniktech.maven.publish")
    val publisher = extensions.create("publisher", Publisher::class.java, project)
    // 使用 afterEvaluate 确保 publish 已被设置
    afterEvaluate {
      val masterDeveloper = publisher.masterDeveloper ?: error("未设置 masterDeveloper")
      extensions.configure<MavenPublishBaseExtension> {
        coordinates(publisher.groupId, publisher.artifactId, publisher.version)
        signAllPublications()
        publishToMavenCentral(true)
        pom {
          name.set(publisher.artifactId)
          description.set(publisher.description)
          url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}")

          publisher.license?.let {
            licenses {
              license {
                name.set(it)
                url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}/blob/${publisher.mainBranch}/LICENSE")
              }
            }
          }

          developers {
            developer {
              masterDeveloper.config(this)
            }
            publisher.otherDevelopers.forEach {
              developer { it.config(this) }
            }
          }

          scm {
            url.set("https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}")
            connection.set("scm:git:git:https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}.git")
            developerConnection.set("scm:git:ssh:https://github.com/${publisher.githubName}/${publisher.githubRepositoryName}.git")
          }
        }
      }
    }
  }
}