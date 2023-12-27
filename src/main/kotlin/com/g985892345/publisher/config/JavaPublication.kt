package com.g985892345.publisher.config

import com.g985892345.publisher.MavenPublicationConfig
import com.g985892345.publisher.configJarTask
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

/**
 * .
 *
 * @author 985892345
 * 2023/12/26 16:48
 */
object JavaPublication : MavenPublicationConfig {
  override fun config(project: Project) {
    super.config(project)
    configJarTask(project)
  }

  override fun MavenPublication.configMaven(project: Project) {
    artifact(project.tasks["javadocJar"])
    artifact(project.tasks["sourcesJar"])
    from(project.components["java"])
  }
}