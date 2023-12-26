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

  object Android : MavenPublicationConfig {
    override fun config(project: Project) {
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

  object Java : MavenPublicationConfig {
    override fun config(project: Project) {
      configJvmTask(project)
    }

    override fun MavenPublication.configMaven(project: Project) {
      artifact(project.tasks["javadocJar"])
      artifact(project.tasks["sourcesJar"])
      from(project.components["java"])
    }
  }

  object KotlinJvm : MavenPublicationConfig {
    override fun config(project: Project) {
      configJvmTask(project)
    }

    override fun MavenPublication.configMaven(project: Project) {
      artifact(project.tasks["javadocJar"])
      artifact(project.tasks["sourcesJar"])
      from(project.components["kotlin"])
    }
  }

  object KotlinMultiplatform : MavenPublicationConfig {
    override fun MavenPublication.configMaven(project: Project) {
      from(project.components["kotlin"])
    }
  }
}

private fun configJvmTask(project: Project) {
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
