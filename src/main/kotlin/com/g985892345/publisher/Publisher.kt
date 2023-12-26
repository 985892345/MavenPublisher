package com.g985892345.publisher

import DeveloperInformation
import com.g985892345.publisher.config.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import java.io.File

/**
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @date 2022/10/10 16:57
 */
abstract class Publisher(val project: Project) {

  // 主开发者信息
  var masterDeveloper = DeveloperInformation(
    "985892345",
    "GuoXiangrui",
    "guo985892345@formail.com"
  )

  // 其他开发者信息
  var otherDevelopers: List<DeveloperInformation> = emptyList()

  // 仓库所有者名字
  var githubName = masterDeveloper.githubName

  // github 仓库名称
  var githubRepositoryName: String = project.rootProject.name

  // github 主分支
  var mainBranch: String = getGitBranchName()

  // 描述
  var description: String = ""

  // 依赖名称
  var artifactId: String = project.name

  /**
   * 依赖的域名
   * 1. 先从当前模块拿
   * 2. 再从 .properties 里面找
   * 3. 最后直接默认 io.github.${githubName}
   */
  var groupId: String = ""
    get() = field.ifEmpty {
      project.group.toString().takeIf { it.isNotEmpty() }
        ?: project.properties["GROUP"]?.toString()
        ?: "io.github.$githubName"
    }

  // 如果版本名以 SNAPSHOT 结尾，则发布在 mavenCentral 的快照仓库
  /**
   * 依赖的版本号
   * 1. 先从当前模块拿
   * 2. 再从 .properties 里面找
   * 3. 还是没得就会报错
   */
  var version: String? = null
    get() = if (field.isNullOrEmpty()) field else {
      project.version.toString().takeIf { it != Project.DEFAULT_VERSION }
        ?: project.properties["VERSION"]?.toString()
    }

  // 开源协议证书
  var license: String? = null

  // 模块配置
  val publicationConfig: MavenPublicationConfig by lazy {
    when {
      project.plugins.hasPlugin("com.android.library") -> AndroidPublication
      project.plugins.hasPlugin("com.gradle.plugin-publish") -> GradlePluginPublication
      project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> KotlinMultiplatformPublication
      project.plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> KotlinJvmPublication
      project.plugins.hasPlugin("java") -> JavaPublication
      else -> error("无法判断模块类型，请将 MavenPublisher 插件放置插件引入的末尾")
    }
  }

  /**
   * gradle 插件需要单独设置
   *
   * 请在设置 githubRepositoryName 和 description 后调用
   */
  fun createGradlePlugin(
    id: String,
    implementationClass: String,
    displayName: String,
    tags: List<String>
  ) {
    project.extensions.configure<GradlePluginDevelopmentExtension> {
      website.set("https://github.com/985892345/${githubRepositoryName}")
      vcsUrl.set("https://github.com/985892345/${githubRepositoryName}")
      plugins {
        create(id) {
          this.id = id
          this.implementationClass = implementationClass
          this.displayName = displayName
          description = this@Publisher.description
          this.tags.set(tags)
        }
      }
    }
    project.afterEvaluate {
      project.tasks.named("toMavenCentral") {
        dependsOn(project.tasks.named("publish${id.capitalize()}PluginMarkerMavenPublicationToMavenCentralRepository"))
      }
      project.tasks.named("toMavenLocal") {
        dependsOn(project.tasks.named("publish${id.capitalize()}PluginMarkerMavenPublicationToMavenLocal"))
      }
    }
  }

  private fun String.capitalize(): String {
    return replaceFirstChar { it.uppercase() }
  }

  private fun getGitBranchName(): String {
    val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
      .directory(File(System.getProperty("user.dir")))
      .start()
    process.inputStream.bufferedReader().use { reader ->
      return reader.readLine().trim()
    }
  }
}