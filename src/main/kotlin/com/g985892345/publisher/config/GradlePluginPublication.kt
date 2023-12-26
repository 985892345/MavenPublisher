package com.g985892345.publisher.config

import com.g985892345.publisher.MavenPublicationConfig
import com.g985892345.publisher.configJarTask
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

/**
 * 使用前还需要单独引入以下插件:
 * ```
 * plugins {
 *   id("com.gradle.plugin-publish") version "1.2.1" // https://plugins.gradle.org/docs/publish-plugin
 *   // 在它下方使用本插件
 * }
 * ```
 * 然后在 build.gradle.kts 中设置 plugin 配置:
 * ```
 * publisher {
 *   createGradlePlugin(
 *     // ...
 *   )
 * }
 * ```
 *
 * @author 985892345
 * 2023/12/26 16:50
 */
object GradlePluginPublication : MavenPublicationConfig {

  override fun config(project: Project) {
    configJarTask(project)
  }

  override fun MavenPublication.configMaven(project: Project) {
    artifact(project.tasks["javadocJar"])
    artifact(project.tasks["sourcesJar"])
    from(project.components["kotlin"])
  }
}

