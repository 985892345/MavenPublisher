package com.g985892345.publisher

import DeveloperInformation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
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
  var masterDeveloper: DeveloperInformation? = null

  // 其他开发者信息
  var otherDevelopers: List<DeveloperInformation> = emptyList()

  // 仓库所有者名字
  var githubName: String = ""
    get() = field.ifEmpty {
      masterDeveloper?.githubName ?: error("未设置 githubName")
    }

  // github 仓库名称
  var githubRepositoryName: String = ""
    get() = field.ifEmpty { project.rootProject.name }

  // github 主分支
  var mainBranch: String = ""
    get() = field.ifEmpty { getGitBranchName() }

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
        ?: project.rootProject.version.toString().takeIf { it != Project.DEFAULT_VERSION }
        ?: project.rootProject.properties["VERSION"]?.toString()?.takeIf { it.isNotEmpty() }
        ?: project.gradle.parent?.rootProject?.version?.toString()?.takeIf { it != Project.DEFAULT_VERSION }
        ?: project.gradle.parent?.rootProject?.properties?.get("VERSION")?.toString()?.takeIf { it.isNotEmpty() }
        ?: error("未设置 version, 建议在 build.gradle 中设置 version 或者 gradle.properties 中设置 VERSION 字段")
    }

  // 开源协议文件
  var licenseFile: () -> File = {
    // 当前项目路径下
    val projectLicenseFile = project.projectDir.resolve("LICENSE")
    if (projectLicenseFile.exists()) projectLicenseFile else {
      // 根项目路径下
      val rootProjectLicenseFile = project.rootProject.projectDir.resolve("LICENSE")
      if (rootProjectLicenseFile.exists()) rootProjectLicenseFile else {
        // 如果是根项目下 plugin 项目，则通过 gradle.parent.rootProject 进行查找
        val parentGradleProjectLicenseFile = project.gradle.parent?.rootProject?.rootDir?.resolve("LICENSE")
        if (parentGradleProjectLicenseFile?.exists() == true) parentGradleProjectLicenseFile else {
          error("请指定 LICENSE 文件路径")
        }
      }
    }
  }

  // 开源协议类型
  var license: String? = null
    get() = field ?: getLicense(licenseFile())

  /**
   * 还需要依赖 `java-gradle-plugin` 才能正常使用
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
    project.apply(plugin = "org.gradle.java-gradle-plugin")
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
    runCatching {
      val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
        .directory(File(System.getProperty("user.dir")))
        .start()
      process.inputStream.bufferedReader().use { reader ->
        return reader.readLine().trim()
      }
    }.getOrElse {
      throw RuntimeException("获取 git 分支名失败", it)
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