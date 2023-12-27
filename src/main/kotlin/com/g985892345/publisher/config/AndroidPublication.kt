package com.g985892345.publisher.config

import com.g985892345.publisher.MavenPublicationConfig
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

/**
 * .
 *
 * @author 985892345
 * 2023/12/26 16:48
 */
object AndroidPublication : MavenPublicationConfig {
  override fun config(project: Project) {
    super.config(project)
    val android = project.extensions.getByName("android")
    val publishing = android.javaClass.getMethod("getPublishing").invoke(android)
    publishing.javaClass.getMethod("singleVariant", String::class.java, Function1::class.java)
      .invoke(publishing, "release", object : Function1<Any, Unit> {
        override fun invoke(p1: Any) {
          p1.javaClass.getMethod("withJavadocJar").invoke(p1)
          p1.javaClass.getMethod("withSourcesJar").invoke(p1)
        }
      })
    /**
     * 实现：
     * android {
     *   publishing {
     *     singleVariant("release") {
     *       withJavadocJar()
     *       withSourcesJar()
     *     }
     *   }
     * }
     */
  }

  override fun MavenPublication.configMaven(project: Project) {
    from(project.components["release"])
  }
}