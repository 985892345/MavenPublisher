package com.g985892345.publisher.config

import com.g985892345.publisher.MavenPublicationConfig
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
  override fun MavenPublication.configMaven(project: Project) {
    from(project.components["kotlin"])
  }
}