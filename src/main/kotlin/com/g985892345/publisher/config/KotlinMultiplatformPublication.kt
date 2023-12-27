package com.g985892345.publisher.config

import com.g985892345.publisher.MavenPublicationConfig
import com.g985892345.publisher.Publisher
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

/**
 * .
 *
 * @author 985892345
 * 2023/12/26 16:50
 */
object KotlinMultiplatformPublication : MavenPublicationConfig {

  override fun config(project: Project) {
    val publisher = project.extensions.create("publisher", Publisher::class.java, project)

  }
  override fun MavenPublication.configMaven(project: Project) {
    from(project.components["kotlin"])
  }
}