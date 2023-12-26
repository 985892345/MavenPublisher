package com.g985892345.publisher

import org.gradle.api.Project

/**
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @date 2022/10/10 16:57
 */
abstract class Publisher(project: Project) {

  var artifactId: String = project.name
  var githubName: String = project.rootProject.name
  var mainBranch: String = "master"
  var description: String = ""

  var groupId = project.properties["GROUP"]?.toString() ?: project.group.toString().takeIf { it.isNotEmpty() }

  // 如果版本名以 SNAPSHOT 结尾，则发布在 mavenCentral 的快照仓库
  var version = project.properties["VERSION"]?.toString() ?: project.version.toString().takeIf { it != Project.DEFAULT_VERSION }

  var license: String? = null

  var publicationConfig : MavenPublicationConfig = when {
    project.plugins.hasPlugin("com.android.library") -> MavenPublicationConfig.Android
    project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> MavenPublicationConfig.KotlinMultiplatform
    project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")  -> MavenPublicationConfig.KotlinJvm
    project.plugins.hasPlugin("java") -> MavenPublicationConfig.Java
    else -> error("未实现")
  }
}