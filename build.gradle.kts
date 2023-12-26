plugins {
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "1.2.1" // https://plugins.gradle.org/docs/publish-plugin
  id("io.github.985892345.MavenPublisher") version "1.0.0-alpha23-SNAPSHOT"
}

group = "io.github.985892345"
version = "1.0.0-alpha23-SNAPSHOT"

repositories {
  mavenLocal()
  // mavenCentral 快照仓库
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  mavenCentral()
}

publisher {
  license = "MIT"
  description = "一键发布 985892345 的开源库到 mavenCentral"
  createGradlePlugin(
    id = "io.github.985892345.MavenPublisher",
    implementationClass = "com.g985892345.publisher.MavenPublisherExtension",
    displayName = "Maven 发布插件",
    tags = listOf("mavenCentral", "Publisher")
  )
}