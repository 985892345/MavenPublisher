package com.g985892345.publisher

import DeveloperInformation
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
    githubName = "985892345",
    email = "guo985892345@formail.com"
  )

  // 其他开发者信息
  var otherDevelopers: List<DeveloperInformation> = emptyList()

  // 仓库所有者名字
  var githubName = masterDeveloper.githubName

  // github 仓库名称
  var githubRepositoryName: String = ""
    get() = field.ifEmpty { project.rootProject.name }

  // github 主分支
  var mainBranch: String = getGitBranchName()

  // 描述
  var description: String = ""

  // 依赖名称
  var artifactId: String = ""
    get() = field.ifEmpty { project.name }

  /**
   * 依赖的域名，io.github.${githubName}
   */
  val groupId: String
    get() = "io.github.$githubName"

  // 如果版本名以 SNAPSHOT 结尾，则发布在 mavenCentral 的快照仓库
  /**
   * 依赖的版本号
   * 1. 先从当前模块拿
   * 2. 再从 .properties 里面找
   * 3. 还是没得就会报错
   */
  var version: String = ""
    get() = field.ifEmpty {
      project.version.toString().takeIf { it != Project.DEFAULT_VERSION }
        ?: project.properties["VERSION"]?.toString()?.takeIf { it.isNotEmpty() }
        ?: error("未设置 version, 建议在 build.properties 中设置 VERSION 字段")
    }

  // 开源协议类型
  var license: String? = null
    get() = field?.let {
      project.rootProject.projectDir
        .resolve("LICENSE")
        .let { getLicense(it) }
    }

  /**
   * gradle 插件需要单独设置
   *
   * 请在设置 githubRepositoryName 和 description 后调用
   * @param name 生成的 gradle task 部分名称
   * @param id 插件 id
   * @param implementationClass 实现类名称
   * @param displayName 插件名称
   * @param tags 插件标签
   */
  fun createGradlePlugin(
    name: String,
    id: String,
    implementationClass: String,
    displayName: String,
    tags: List<String>
  ) {
    project.extensions.configure<GradlePluginDevelopmentExtension> {
      website.set("https://github.com/${githubName}/${githubRepositoryName}")
      vcsUrl.set("https://github.com/${githubName}/${githubRepositoryName}")
      plugins {
        create(name) {
          this.id = id
          this.implementationClass = implementationClass
          this.displayName = displayName
          description = this@Publisher.description
          this.tags.set(tags)
        }
      }
    }
  }

  private fun getGitBranchName(): String {
    val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
      .directory(File(System.getProperty("user.dir")))
      .start()
    process.inputStream.bufferedReader().use { reader ->
      return reader.readLine().trim()
    }
  }

  private fun getLicense(file: File): String? {
    if (!file.exists()) return null
    if (!file.canRead()) return null
    val text = file.readText()
    return when {
      text.containsAll("Apache License", "Version 2.0") -> "Apache License Version 2.0"
      text.containsAll("GNU GENERAL PUBLIC LICENSE", "Version 2") -> "GNU General Public License v2.0"
      text.containsAll("GNU GENERAL PUBLIC LICENSE", "Version 3") -> "GNU General Public License v3.0"
      text.contains("MIT License") -> "MIT License"
      text.contains("BSD 2-Clause License") -> "BSD 2-Clause License"
      text.contains("BSD 3-Clause License") -> "BSD 3-Clause License"
      text.contains("Boost Software License - Version 1.0") -> "Boost Software License 1.0"
      text.containsAll("Creative Commons Legal Code", "CC0 1.0 Universal") -> "Creative Commons Zero v1.0 Universal"
      text.contains("Eclipse Public License - v 2.0") -> "Eclipse Public License 2.0"
      text.containsAll("GNU AFFERO GENERAL PUBLIC LICENSE", "Version 3") -> "GNU Affero General Public License v3.0"
      text.containsAll("GNU LESSER GENERAL PUBLIC LICENSE", "Version 2.1") -> "GNU Lesser General Public License v2.1"
      text.containsAll("Mozilla Public License Version 2.0") -> "Mozilla Public License 2.0"
      text.containsAll("This is free and unencumbered software released into the public domain.") -> "The Unlicense"
      else -> null
    }
  }

  private fun String.containsAll(vararg text: String): Boolean {
    return text.all { contains(it) }
  }
}