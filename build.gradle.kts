plugins {
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "1.2.1" // https://plugins.gradle.org/docs/publish-plugin
}

group = "io.github.985892345"
version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {

}

gradlePlugin {
  website.set("https://github.com/985892345/KtProvider")
  vcsUrl.set("https://github.com/985892345/KtProvider")
  plugins {
    create("MavenPublisher") {
      id = "io.github.985892345.MavenPublisher"
      implementationClass = "com.g985892345.publisher.MavenPublisherExtension"
      displayName = "Maven 发布插件"
      description = "一键发布 985892345 的开源库到 mavenCentral"
      tags.set(listOf("mavenCentral", "Publisher"))
    }
  }
}

publishing {
  publications {
    val projectArtifactId = "MavenPublisher"
    val projectGroupId = project.group.toString()
    val projectVersion = project.version.toString()
    val projectGithubName = "985892345"
    val projectDescription = "一键发布 985892345 的开源库到 mavenCentral"
    val projectMainBranch = ""
    create<MavenPublication>("MavenCentral") {
      artifactId = projectArtifactId
      groupId = projectGroupId
      version = projectVersion
      publish.publicationConfig.apply { configMaven(project) }
      extensions.configure<SigningExtension> {
        sign(this@create)
      }

      pom {
        name.set(projectArtifactId)
        description.set(projectDescription)
        url.set("https://github.com/985892345/$projectGithubName")

        licenses {
          license {
            name.set(publish.license ?: error("未提供 License"))
            url.set("https://github.com/985892345/$projectGithubName/blob/$projectMainBranch/LICENSE")
          }
        }

        developers {
          developer {
            id.set("985892345")
            name.set("GuoXiangrui")
            email.set("guo985892345@formail.com")
          }
        }

        scm {
          connection.set("https://github.com/985892345/$projectGithubName.git")
          developerConnection.set("https://github.com/985892345/$projectGithubName.git")
          url.set("https://github.com/985892345/$projectGithubName")
        }
      }
    }
    repositories {
      maven {
        // https://s01.oss.sonatype.org/
        name = "mavenCentral"
        val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        val isSnapshot = projectVersion.endsWith("SNAPSHOT")
        setUrl(if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl)
        credentials {
          username = project.properties["mavenCentralUsername"].toString()
          password = project.properties["mavenCentralPassword"].toString()
        }
      }
    }
  }
  publications {
    repositories {
      maven {
        // https://s01.oss.sonatype.org/
        name = "mavenCentral"
        val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        val isSnapshot = version.toString().endsWith("SNAPSHOT")
        setUrl(if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl)
        credentials {
          username = project.properties["mavenCentralUsername"].toString()
          password = project.properties["mavenCentralPassword"].toString()
        }
      }
    }
  }
}


