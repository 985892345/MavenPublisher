package com.g985892345.publisher

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get

/**
 * .
 *
 * @author 985892345
 * 2023/12/25 22:35
 */
interface MavenPublicationConfig {
  fun config(project: Project) {}
  fun MavenPublication.configMaven(project: Project)
  fun afterConfigMaven(project: Project) {
    project.tasks.register("toMavenCentral") {
      group = "maven publisher"
      dependsOn("publishMavenCentralPublicationToMavenCentralRepository")
    }

    project.tasks.register("toMavenLocal") {
      group = "maven publisher"
      dependsOn("publishMavenCentralPublicationToMavenLocal")
    }
  }
}

internal fun configJarTask(project: Project) {
  project.tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
    from("javadoc")
  }

  project.tasks.register("sourcesJar", Jar::class.java) {
    archiveClassifier.set("sources")
    val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
    from(sourceSets["main"].allSource)
  }
}
